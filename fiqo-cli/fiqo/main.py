import typer
import getpass
import questionary
from fiqo import client

app = typer.Typer()


@app.command()
def login(
    username=typer.Option(None, "--username", "-u", help="Username"),
    password=typer.Option(None, "--password", "-p", help="Password"),
):
    try:
        if username is None:
            username = input("Username: ")

        if password is None:
            password = getpass.getpass("Password: ")

        client.login(username, password)
    except Exception as e:
        print(f"{e}")
        raise typer.Exit(code=1)


@app.command()
def register(
    username=typer.Option(None, "--username", "-u", help="Username"),
    email=typer.Option(None, "--email", "-e", help="Email"),
    password=typer.Option(None, "--password", "-p", help="Password"),
):
    try:
        if username is None:
            username = input("Username: ")

        if email is None:
            email = input("Email: ")

        if password is None:
            password = getpass.getpass("Password: ")

        client.register(username, email, password)

    except Exception as e:
        print(f"{e}")
        raise typer.Exit(code=1)


@app.command()
def push(resource: str, target: str = typer.Argument("./")):
    try:
        client.push(target, resource)
    except Exception as e:
        print(f"{e}")
        raise typer.Exit(code=1)


@app.command()
def pull(
    resource: str,
    target: str = typer.Argument("./"),
    option: bool = typer.Option(False, "--select", "-s", help="Select"),
):
    try:
        if option:
            select(target, resource)
        else:
            client.pull(target, resource)

    except Exception as e:
        print(f"{e}")
        raise typer.Exit(code=1)


@app.command("rm")
@app.command("remove")
def remove(
    target: str,
    recursive: bool = typer.Option(
        False,
        "--recursive",
        "-r",
        help="Remove file or directory",
    ),
):
    try:
        client.remove(target, recursive)
    except Exception as e:
        print(f"{e}")
        raise typer.Exit(code=1)


@app.command()
def ls(path: str = typer.Argument(".", help="Path to list")):
    try:
        result = client.list(path)
        for file in result:
            print(f"📄 {file['name']}")
    except Exception as e:
        print(f"{e}")
        raise typer.Exit(code=1)


def select(target: str, resource: str):
    files = client.list(resource)

    file = questionary.select(f"Select a file for downloading", choices=files).ask()

    if file:
        client.pull(target, resource + "/" + file)
    else:
        print("⚠️  No selection made.")


if __name__ == "__main__":
    app()
