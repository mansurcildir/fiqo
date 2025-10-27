import PageMeta from '../../components/common/PageMeta';
import AuthLayout from './AuthPageLayout';
import PasswordRecoveryForm from '../../components/auth/PassworRecoveryForm';

export default function ForgotPassword() {
  return (
    <>
      <PageMeta title="fiqo | Forgot Password" description="Forgot Password Page" />
      <AuthLayout>
        <PasswordRecoveryForm />
      </AuthLayout>
    </>
  );
}
