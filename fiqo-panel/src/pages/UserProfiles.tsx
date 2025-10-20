import UserMetaCard from '../components/UserProfile/UserMetaCard';
import UserAccount from '../components/UserProfile/UserAccount';
import PageMeta from '../components/common/PageMeta';
import UserInfoCard from '../components/UserProfile/UserInfoCard';
import { useEffect, useState } from 'react';
import { UserInfo } from '../model/user/UserInfo';
import { userAPI } from '../service/user-service';
import { useAlert } from '../service/alert-service';

export default function UserProfiles() {
  const { showAlert } = useAlert();
  const [avatarSrc, setAvatarSrc] = useState<string>('/images/user/user-01.jpg');
  const [userInfo, setUserInfo] = useState<UserInfo>({
    uuid: '',
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    phone: '',
    bio: '',
    facebookUrl: '',
    xUrl: '',
    linkedinUrl: '',
    instagramUrl: '',
    totalFileSize: 0
  });

  const fetchProfile = async () => {
    userAPI
      .getProfile()
      .then((res) => {
        setUserInfo(res.data);
      })
      .catch((err) => {
        showAlert(err, 'error');
      });
  };

  const getAvatar = async () => {
    await userAPI
      .getAvatar()
      .then((buffer) => {
        if (!buffer || buffer.byteLength === 0) {
          return;
        }
        const base64 = btoa(new Uint8Array(buffer).reduce((data, byte) => data + String.fromCharCode(byte), ''));
        setAvatarSrc(`data:image/png;base64,${base64}`);
      })
      .catch((err) => {
        showAlert(err, 'error');
      });
  };

  useEffect(() => {
    fetchProfile();
    getAvatar();
  }, []);
  return (
    <>
      <PageMeta title="fiqo | Profile" description="Profile Page" />
      <div className="rounded-2xl border border-gray-200 bg-white p-5 lg:p-6 dark:border-gray-800 dark:bg-white/[0.03]">
        <h3 className="mb-5 text-lg font-semibold text-gray-800 lg:mb-7 dark:text-white/90">Profile</h3>
        <div className="space-y-6">
          <UserMetaCard userInfo={userInfo} fetchProfile={fetchProfile} avatarSrc={avatarSrc} />
          <UserInfoCard userInfo={userInfo} fetchProfile={fetchProfile} />
        </div>
      </div>
      <UserAccount />
    </>
  );
}
