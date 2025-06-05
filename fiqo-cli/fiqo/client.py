import requests
from pathlib import Path
import os
import jwt
import time
from dotenv import load_dotenv

load_dotenv()

BASE_URL = os.getenv("BASE_URL", "http://localhost:8080")
FIQORC_PATH = Path.home() / ".fiqorc"


def login(username: str, password: str):
    url = f"{BASE_URL}/v1/auth/login"
    payload = {"username": username, "password": password}

    res = requests.post(url, json=payload)
    if res.status_code != 200:
        raise Exception(f"⚠️  {res.status_code} {res.json()["message"]}")

    access_token = res.json().get("access_token")
    if not access_token:
        raise Exception("⚠️  No token received")

    refresh_token = res.json().get("refresh_token")
    if not refresh_token:
        raise Exception("⚠️  No token received")

    with open(FIQORC_PATH, "w") as f:
        f.write(f"accessToken={access_token}\n")
        f.write(f"refreshToken={refresh_token}\n")

    print(f"\n✅ Login successful")


def register(username: str, email: str, password: str):
    url = f"{BASE_URL}/v1/auth/register"
    payload = {"username": username, "email": email, "password": password}

    res = requests.post(url, json=payload)
    if res.status_code != 201:
        raise Exception("Register failed:", res.text)

    print(f"\n✅ Register successful")


def refresh(refresh_token: str):
    url = f"{BASE_URL}/v1/auth/refresh"
    headers = {"Refresh-Token": f"{refresh_token}"}

    res = requests.get(url, headers=headers)

    if res.status_code == 200:
        new_access = res.json().get("access_token")

        with open(FIQORC_PATH, "w") as f:
            f.write(f"accessToken={new_access}\n")
            f.write(f"refreshToken={refresh_token}\n")
        return

    raise Exception(f"⚠️  {res.status_code} {res.json()["message"]}")


def get_auth_token():
    if not FIQORC_PATH.exists():
        raise Exception("⚠️  .fiqorc file does not exist")

    tokens = {}
    with open(FIQORC_PATH, "r") as f:
        for line in f:
            if "=" in line:
                k, v = line.strip().split("=", 1)
                tokens[k] = v

    access_token: str = tokens.get("accessToken")
    refresh_token: str = tokens.get("refreshToken")

    if access_token and not is_jwt_expired(access_token):
        return access_token

    elif refresh_token and not is_jwt_expired(refresh_token):
        refresh(refresh_token)
        return get_auth_token()


def is_jwt_expired(token: str) -> bool:
    try:
        payload = jwt.decode(token, options={"verify_signature": False})
        exp = payload.get("exp")
        if not exp:
            return True
        return time.time() > exp
    except Exception as e:
        return True


def get_file(path: str):
    token = get_auth_token()

    url = f"{BASE_URL}/v1/files/file-info"
    headers = {"Authorization": f"Bearer {token}"}
    params = {"path": path}

    res = requests.get(url, headers=headers, params=params, stream=True)

    if res.status_code != 200:
        raise Exception(f"⚠️  {res.status_code} {res.json()["message"]}")

    return res.json()


def list(path: str):
    token = get_auth_token()

    url = f"{BASE_URL}/v1/files/info"
    headers = {"Authorization": f"Bearer {token}"}
    params = {"path": path}

    res = requests.get(url, headers=headers, params=params, stream=True)

    if res.status_code != 200:
        raise Exception(f"⚠️  {res.status_code} {res.json()["message"]}")

    return res.json().get("data")


def upload_file(target: str, resource: str):
    token = get_auth_token()
    url = f"{BASE_URL}/v1/files?path={target}"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/octet-stream",
    }

    with open(resource, "rb") as f:
        res = requests.put(url, headers=headers, data=f)

    if res.status_code != 200:
        raise Exception(f"⚠️  {res.status_code} {res.json()["message"]} ")

    print(f"⬆️  {target}")


def push(target: str, resource: str):
    resource_path = Path(resource)
    target_path = Path(target)

    if resource_path.is_file():
        dest_path = target_path / resource_path.name
        upload_file(dest_path, resource_path)
        print(f"\n✅ Pushed successfully")

    elif resource_path.is_dir():
        for root, dirs, files in os.walk(resource):
            for file in files:
                full_path = Path(root) / file
                relative_path = full_path.relative_to(resource_path.parent)
                dest_path = target_path / relative_path.as_posix()
                upload_file(str(dest_path), full_path)
        print(f"\n✅ Pushed successfully")

    else:
        raise Exception(f"⚠️  {resource} is not a file or directory")


def download_file(target: str, resource: str):
    token = get_auth_token()
    url = f"{BASE_URL}/v1/files?path={resource}"
    headers = {"Authorization": f"Bearer {token}"}

    res = requests.get(url, headers=headers, stream=True)

    if res.status_code != 200:
        raise Exception(f"⚠️  {res.status_code} {res.json()["message"]} ")

    with open(target, "wb") as f:
        for chunk in res.iter_content(chunk_size=8192):
            if chunk:
                f.write(chunk)

    print(f"⬇️  {target}")


def pull(target: str, resource: str):
    files = list(resource)

    if not files:
        print(f"⚠️  No files found at resource path: {resource}")
        return

    resource_path = Path(resource)
    resource_name = resource_path.name

    for file in files:
        file_path: str = file["path"]
        suffix = file_path.partition(str(resource_path))[2].lstrip("/\\")
        relative = (
            Path(resource_name) / Path(suffix) if Path(suffix) else Path(resource_name)
        )

        target_path = Path(target) / relative
        target_path.parent.mkdir(parents=True, exist_ok=True)

        download_file(target_path, file_path)

    print(f"\n✅ Pulled successfuly")


def remove(path: str, recursive: bool):
    token = get_auth_token()

    url = f"{BASE_URL}/v1/files"
    headers = {"Authorization": f"Bearer {token}"}
    params = {"path": path, "recursive": recursive}

    res = requests.delete(url, headers=headers, params=params, stream=True)

    if res.status_code != 200:
        raise Exception(f"⚠️  {res.status_code} {res.json()["message"]} ")

    print(f"\n✅ Removed successfully")
