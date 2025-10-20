import { Modal } from '../ui/modal';
import Button from '../ui/button/Button';
import Input from '../form/input/InputField';
import Label from '../form/Label';
import { Resolver, useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { userAPI } from '../../service/user-service';
import { UserInfo } from '../../model/user/UserInfo';
import { ProfileForm } from '../../model/user/ProfileForm';
import { useEffect } from 'react';
import { ResetPasswordForm } from '../../model/user/ResetPasswordForm';
import { normalizeEntries } from '../../utils/utils';
import { authAPI } from '../../service/auth-service';
import { useAlert } from '../../service/alert-service';

interface Props {
  userInfo: UserInfo;
  fetchProfile: () => Promise<void>;
  isOpen: boolean;
  closeModal: () => void;
}

const personalSchema = yup.object().shape({
  username: yup.string().required('Username is required').min(3, 'Username should have at least 3 characters'),
  email: yup.string().required('Email is required').email('Email should be valid format'),

  firstName: yup.string().nullable(),
  lastName: yup.string().nullable(),
  phone: yup.string().nullable(),
  bio: yup.string().nullable(),
  facebookUrl: yup.string().nullable().url('Facebook URL must be a valid URL'),
  xUrl: yup.string().nullable().url('X URL must be a valid URL'),
  linkedinUrl: yup.string().nullable().url('Linkedin URL must be a valid URL'),
  instagramUrl: yup.string().nullable().url('Instagram URL must be a valid URL')
});

const resetPasswordSchema = yup.object().shape({
  password: yup.string().required('Password is required').min(6, 'Password should be at least 6 character'),
  confirmPassword: yup
    .string()
    .required('Confirm password is required')
    .min(6, 'Password should be at least 6 character')
});

export default function ProfileModal({ userInfo, fetchProfile, isOpen, closeModal }: Props) {
  const { showAlert } = useAlert();

  const personalForm = useForm<ProfileForm>({
    resolver: yupResolver(personalSchema) as Resolver<ProfileForm>,
    defaultValues: {},
    mode: 'all'
  });

  const resetPasswordForm = useForm<ResetPasswordForm>({
    resolver: yupResolver(resetPasswordSchema) as Resolver<ResetPasswordForm>,
    defaultValues: {},
    mode: 'all'
  });

  useEffect(() => {
    clearPersonalForm();
  }, [userInfo]);

  const updateProfile = async (form: ProfileForm) => {
    const normalizedForm = normalizeEntries(form);

    return userAPI
      .updateProfile(normalizedForm)
      .then((res) => {
        fetchProfile();
        showAlert(res.message, 'success', 3000);
      })
      .catch((err) => {
        showAlert(err, 'error');
      });
  };

  const resetPassword = async (form: ResetPasswordForm) => {
    const normalizedForm = normalizeEntries(form);

    return authAPI
      .resetPassword(normalizedForm)
      .then((res) => {
        clearResetPasswordForm();
        showAlert(res.message, 'success', 3000);
      })
      .catch((err) => {
        showAlert(err.response.data.message, 'error');
      });
  };

  const clearPersonalForm = () => {
    personalForm.reset({
      username: userInfo.username,
      email: userInfo.email,
      firstName: userInfo.firstName,
      lastName: userInfo.lastName,
      phone: userInfo.phone,
      bio: userInfo.bio,
      facebookUrl: userInfo.facebookUrl,
      xUrl: userInfo.xUrl,
      linkedinUrl: userInfo.linkedinUrl,
      instagramUrl: userInfo.instagramUrl
    });
  };

  const clearResetPasswordForm = () => {
    resetPasswordForm.reset({
      password: '',
      confirmPassword: ''
    });
  };

  return (
    <>
      <Modal isOpen={isOpen} onClose={closeModal} className="m-4 max-w-[700px]">
        <div className="no-scrollbar relative w-full max-w-[700px] overflow-y-auto rounded-3xl bg-white p-4 lg:p-11 dark:bg-gray-900">
          <div className="px-2 pr-14">
            <h4 className="mb-2 text-2xl font-semibold text-gray-800 dark:text-white/90">Edit Personal Information</h4>
            <p className="mb-6 text-sm text-gray-500 lg:mb-7 dark:text-gray-400">
              Update your details to keep your profile up-to-date.
            </p>
          </div>

          <div className="custom-scrollbar h-[450px] overflow-y-auto px-2 pb-3">
            <form onSubmit={personalForm.handleSubmit(updateProfile)} className="flex flex-col">
              <div>
                <h5 className="mb-5 text-lg font-medium text-gray-800 lg:mb-6 dark:text-white/90">Social Links</h5>

                <div className="grid grid-cols-1 gap-x-6 gap-y-5 lg:grid-cols-2">
                  <div>
                    <Label htmlFor="facebookUrl">Facebook</Label>
                    <Input type="text" id="facebookUrl" {...personalForm.register('facebookUrl')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.facebookUrl ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.facebookUrl?.message ?? ' '}
                    </p>
                  </div>

                  <div>
                    <Label htmlFor="xUrl">X.com</Label>
                    <Input type="text" id="xUrl" {...personalForm.register('xUrl')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.xUrl ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.xUrl?.message ?? ' '}
                    </p>
                  </div>

                  <div>
                    <Label htmlFor="linkedinUrl">Linkedin</Label>
                    <Input type="text" id="linkedinUrl" {...personalForm.register('linkedinUrl')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.linkedinUrl ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.linkedinUrl?.message ?? ' '}
                    </p>
                  </div>

                  <div>
                    <Label htmlFor="instagramUrl">Instagram</Label>
                    <Input type="text" id="instagramUrl" {...personalForm.register('instagramUrl')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.instagramUrl ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.instagramUrl?.message ?? ' '}
                    </p>
                  </div>
                </div>
              </div>
              <div className="mt-7">
                <h5 className="mb-5 text-lg font-medium text-gray-800 lg:mb-6 dark:text-white/90">
                  Personal Information
                </h5>

                <div className="grid grid-cols-1 gap-x-6 gap-y-5 lg:grid-cols-2">
                  <div className="col-span-2 lg:col-span-1">
                    <Label htmlFor="firstName">First Name</Label>
                    <Input type="text" id="firstName" {...personalForm.register('firstName')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.firstName ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.firstName?.message ?? ' '}
                    </p>
                  </div>

                  <div className="col-span-2 lg:col-span-1">
                    <Label htmlFor="lastName">Last Name</Label>
                    <Input type="text" id="lastName" {...personalForm.register('lastName')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.lastName ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.lastName?.message ?? ' '}
                    </p>
                  </div>

                  <div className="col-span-2 lg:col-span-1">
                    <Label htmlFor="username">Username</Label>
                    <Input type="text" id="username" {...personalForm.register('username')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.username ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.username?.message ?? ' '}
                    </p>
                  </div>

                  <div className="col-span-2 lg:col-span-1">
                    <Label htmlFor="email">Email Address</Label>
                    <Input type="email" id="email" {...personalForm.register('email')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.email ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.email?.message ?? ' '}
                    </p>
                  </div>

                  <div className="col-span-2 lg:col-span-1">
                    <Label htmlFor="phone">Phone</Label>
                    <Input type="text" id="phone" {...personalForm.register('phone')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.phone ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.phone?.message ?? ' '}
                    </p>
                  </div>

                  <div className="col-span-2 lg:col-span-1">
                    <Label htmlFor="bio">Bio</Label>
                    <Input type="text" id="bio" {...personalForm.register('bio')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${personalForm.formState.errors.bio ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {personalForm.formState.errors.bio?.message ?? ' '}
                    </p>
                  </div>
                </div>
              </div>
              <div className="mt-6 flex items-center gap-3 px-2 lg:justify-end">
                <div onClick={(e) => e.preventDefault()}>
                  <Button size="sm" variant="outline" onClick={clearPersonalForm}>
                    Clear
                  </Button>
                </div>
                <Button size="sm" disabled={!personalForm.formState.isValid || personalForm.formState.isSubmitting}>
                  Save Changes
                </Button>
              </div>
            </form>
            <form onSubmit={resetPasswordForm.handleSubmit(resetPassword)} className="flex flex-col">
              <div className="mt-7">
                <h5 className="mb-5 text-lg font-medium text-gray-800 lg:mb-6 dark:text-white/90">Reset Password</h5>

                <div className="grid grid-cols-1 gap-x-6 gap-y-5 lg:grid-cols-2">
                  <div className="col-span-2 lg:col-span-1">
                    <Label htmlFor="password">Password</Label>
                    <Input type="password" id="password" {...resetPasswordForm.register('password')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${resetPasswordForm.formState.errors.password ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {resetPasswordForm.formState.errors.password?.message ?? ' '}
                    </p>
                  </div>

                  <div className="col-span-2 lg:col-span-1">
                    <Label htmlFor="confirmPassword">Confirm Password</Label>
                    <Input type="password" id="confirmPassword" {...resetPasswordForm.register('confirmPassword')} />
                    <p
                      className={`text-error-500 h-1 py-1 text-sm transition-all duration-300 ease-in-out ${resetPasswordForm.formState.errors.confirmPassword ? 'opacity-100' : 'opacity-0'}`}
                    >
                      {resetPasswordForm.formState.errors.confirmPassword?.message ?? ' '}
                    </p>
                  </div>
                </div>
              </div>
              <div className="mt-6 flex items-center gap-3 px-2 lg:justify-end">
                <div onClick={(e) => e.preventDefault()}>
                  <Button size="sm" variant="outline" onClick={clearResetPasswordForm}>
                    Clear
                  </Button>
                </div>
                <Button
                  size="sm"
                  disabled={!resetPasswordForm.formState.isValid || resetPasswordForm.formState.isSubmitting}
                >
                  Save Changes
                </Button>
              </div>
            </form>
          </div>
        </div>
      </Modal>
    </>
  );
}
