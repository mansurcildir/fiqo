import { useEffect, useState } from 'react';
import { userAPI } from '../service/user-service';
import UserMetaCard from '../components/UserProfile/UserMetaCard';
import UserAccount from '../components/UserProfile/UserAccount';
import PageMeta from '../components/common/PageMeta';

export default function UserProfiles() {
  const [userInfo, setUserInfo] = useState({
    uuid: '',
    username: '',
    email: ''
  });

  useEffect(() => {
    const fetchProfile = async () => {
      userAPI.getProfile().then((res) => {
        setUserInfo({
          uuid: res.data.uuid,
          username: res.data.username,
          email: res.data.email
        });
      });
    };

    fetchProfile();
  }, []);

  return (
    <>
      <PageMeta
        title="React.js Profile Dashboard | TailAdmin - Next.js Admin Dashboard Template"
        description="This is React.js Profile Dashboard page for TailAdmin - React.js Tailwind CSS Admin Dashboard Template"
      />
      <div className="rounded-2xl border border-gray-200 bg-white p-5 lg:p-6 dark:border-gray-800 dark:bg-white/[0.03]">
        <h3 className="mb-5 text-lg font-semibold text-gray-800 lg:mb-7 dark:text-white/90">Profile</h3>
        <div className="space-y-6">
          <UserMetaCard username={userInfo.username} email={userInfo.email} />
          <UserAccount />
        </div>
      </div>
    </>
  );
}
