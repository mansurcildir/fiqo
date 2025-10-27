import { useEffect, useState } from 'react';
import { Link } from 'react-router';
import { ChevronLeftIcon } from '../../icons';
import Label from '../form/Label';
import Input from '../form/input/InputField';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { authAPI } from '../../service/auth-service';
import { EmailForm } from '../../model/user/EmailForm';
import { useAlert } from '../../service/alert-service';

const validationSchema = yup.object().shape({
  email: yup.string().required('Email is required').email('Email should be valid format')
});

export default function PasswordRecoveryForm() {
  const [loggedIn, setLoggedIn] = useState(false);
  const { showAlert } = useAlert();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting, isValid }
  } = useForm<EmailForm>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      email: ''
    },
    mode: 'all'
  });

  useEffect(() => {
    const loggedIn = localStorage.getItem('access-token');
    setLoggedIn(loggedIn ? true : false);
  }, []);

  const sendVerification = async (form: EmailForm) => {
    return authAPI
      .sendVerificationEmail(form)
      .then((res) => {
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
              Password Recovery
            </h1>
            <p className="text-sm text-gray-500 dark:text-gray-400">Enter your email to send verification code!</p>
          </div>
          <div>
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 sm:gap-5"></div>

            <form onSubmit={handleSubmit(sendVerification)}>
              <div className="space-y-6">
                <div>
                  <Label htmlFor="email">
                    Email<span className="text-error-500">*</span>
                  </Label>
                  <Input type="email" id="email" placeholder="Enter your email" {...register('email')} />
                  <p
                    className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${errors.email ? 'opacity-100' : 'opacity-0'}`}
                  >
                    {errors.email?.message ?? ' '}
                  </p>
                </div>

                <button
                  type="submit"
                  disabled={!isValid || isSubmitting}
                  className="bg-brand-500 shadow-theme-xs hover:bg-brand-600 flex w-full items-center justify-center rounded-lg px-4 py-3 text-sm font-medium text-white transition disabled:opacity-50"
                >
                  Send Verification
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
