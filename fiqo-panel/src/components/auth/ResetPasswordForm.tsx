import { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { ChevronLeftIcon } from '../../icons';
import Label from '../form/Label';
import Input from '../form/input/InputField';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { authAPI } from '../../service/auth-service';
import { RecoverPasswordForm } from '../../model/user/RecoverPasswordForm';
import { useAlert } from '../../service/alert-service';

const validationSchema = yup.object().shape({
  password: yup.string().required('Password is required').min(6, 'Password should be at least 6 character'),
  confirmPassword: yup
    .string()
    .required('Confirm password is required')
    .min(6, 'Password should be at least 6 character'),
  code: yup
    .string()
    .required('Code is required')
    .matches(/^\d{6}$/, 'Code must be 6 digits')
});

export default function ResetPasswordForm() {
  const [loggedIn, setLoggedIn] = useState(false);
  const navigate = useNavigate();
  const { showAlert } = useAlert();
  const [searchParams] = useSearchParams();
  const code = searchParams.get('code');

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors, isSubmitting, isValid }
  } = useForm<RecoverPasswordForm>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      password: '',
      confirmPassword: '',
      code: ''
    },
    mode: 'all'
  });

  useEffect(() => {
    if (code) {
      setValue('code', code);
    }
    const loggedIn = localStorage.getItem('access-token');
    setLoggedIn(loggedIn ? true : false);
  }, []);

  const recoverPassword = async (form: RecoverPasswordForm) => {
    return authAPI
      .recoverPassword(form)
      .then((res) => {
        navigate('/signin');
        showAlert(res.message, 'success');
      })
      .catch((err) => {
        showAlert(err.response.data.message, 'error');
      });
  };

  return (
    <div className="flex flex-1 flex-col">
      <div className="mx-auto w-full max-w-md pt-10">
        <Link
          to="/"
          className={`inline-flex items-center text-sm text-gray-500 transition-colors hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300 ${!loggedIn ? 'invisible' : ''}`}
        >
          <ChevronLeftIcon className="size-5" />
          Back to dashboard
        </Link>
      </div>
      <div className="mx-auto flex w-full max-w-md flex-1 flex-col justify-center">
        <div>
          <div className="mb-5 sm:mb-8">
            <h1 className="text-title-sm sm:text-title-md mb-2 font-semibold text-gray-800 dark:text-white/90">
              Reset Password
            </h1>
            <p className="text-sm text-gray-500 dark:text-gray-400">Enter your email to reset your password!</p>
          </div>
          <div>
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 sm:gap-5"></div>

            <form onSubmit={handleSubmit(recoverPassword)}>
              <div className="space-y-6">
                <div>
                  <Label htmlFor="password">
                    Password<span className="text-error-500">*</span>
                  </Label>
                  <Input type="password" id="password" {...register('password')} />
                  <p className="text-error-500 h-1 py-1 text-sm">{errors.password?.message ?? ''}</p>
                </div>

                <div>
                  <Label htmlFor="confirmPassword">
                    Confirm password<span className="text-error-500">*</span>
                  </Label>
                  <Input type="password" id="confirmPassword" {...register('confirmPassword')} />
                  <p className="text-error-500 h-1 py-1 text-sm">{errors.confirmPassword?.message ?? ''}</p>
                </div>

                <div>
                  <Label htmlFor="code">
                    Code<span className="text-error-500">*</span>
                  </Label>
                  <Input type="text" id="code" {...register('code')} />
                  <p className="text-error-500 h-1 py-1 text-sm">{errors.code?.message ?? ''}</p>
                </div>

                <button
                  type="submit"
                  disabled={!isValid || isSubmitting}
                  className="bg-brand-500 shadow-theme-xs hover:bg-brand-600 flex w-full items-center justify-center rounded-lg px-4 py-3 text-sm font-medium text-white transition disabled:opacity-50"
                >
                  Reset
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
