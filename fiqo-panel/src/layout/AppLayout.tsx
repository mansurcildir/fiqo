import { SidebarProvider } from '../context/SidebarContext';
import { Outlet } from 'react-router';
import AppHeader from './AppHeader';
import Backdrop from './Backdrop';
import AppSidebar from './AppSidebar';
import { useSidebar } from '../utils/utils';
import CircularProgress from '@mui/material/CircularProgress';
import { useEffect, useState } from 'react';
import { authAPI } from '../service/auth-service';

const LayoutContent: React.FC = () => {
  const { isExpanded, isHovered, isMobileOpen } = useSidebar();

  return (
    <div className="min-h-screen xl:flex">
      <div>
        <AppSidebar />
        <Backdrop />
      </div>
      <div
        className={`flex-1 transition-all duration-300 ease-in-out ${
          isExpanded || isHovered ? 'lg:ml-[290px]' : 'lg:ml-[90px]'
        } ${isMobileOpen ? 'ml-0' : ''}`}
      >
        <AppHeader />
        <div className="mx-auto max-w-(--breakpoint-2xl) p-4 md:p-6">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

const AppLayout: React.FC = () => {
  const [authorized, setAuthorized] = useState<boolean>(false);

  useEffect(() => {
    setAuthorized(false);
    authAPI.authorize().then(() => {
      setAuthorized(true);
    });
  });

  return authorized ? (
    <SidebarProvider>
      <LayoutContent />
    </SidebarProvider>
  ) : (
    <div className="flex h-screen items-center justify-center">
      <CircularProgress />
    </div>
  );
};

export default AppLayout;
