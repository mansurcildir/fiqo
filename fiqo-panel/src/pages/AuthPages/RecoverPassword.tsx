import ResetPasswordForm from '../../components/auth/ResetPasswordForm';
import PageMeta from '../../components/common/PageMeta';
import AuthLayout from './AuthPageLayout';

export default function RecoverPassword() {
  return (
    <>
      <PageMeta title="fiqo | Reset Password" description="Reset Password Page" />
      <AuthLayout>
        <ResetPasswordForm />
      </AuthLayout>
    </>
  );
}
