import { DataResult } from '../model/result/DataResult';
import { LoginReq } from '../model/user/LoginReq';
import { LoginRes } from '../model/user/LoginRes';
import { UserForm } from '../model/user/UserForm';
import { SPRING_BASE_URL } from '../utils/utils';

import axios from 'axios';
import { clearTokens, getAccessToken, getAllTokens, getRefreshToken } from './storage-manager';
import { isTokenExpired } from './token-decoder';
import { ResetPasswordForm } from '../model/user/ResetPasswordForm';
import { Result } from '../model/result/Result';

export const authAPI = {
  login: async (body: LoginReq): Promise<DataResult<LoginRes>> => {
    const response = await axios.post(`${SPRING_BASE_URL}/v1/auth/login`, body);
    return response.data;
  },

  register: async (body: UserForm): Promise<DataResult<LoginRes>> => {
    const response = await axios.post(`${SPRING_BASE_URL}/v1/auth/register`, body);
    return response.data;
  },

  logout: async (): Promise<void> => {
    const accessToken = getAccessToken();
    await axios.get(`${SPRING_BASE_URL}/v1/auth/logout`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    clearTokens();
  },

  getAccessToken: async (): Promise<DataResult<LoginRes>> => {
    const refreshToken = getRefreshToken();
    const response = await axios.get(`${SPRING_BASE_URL}/v1/auth/refresh`, {
      headers: {
        'Refresh-Token': refreshToken,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  refreshToken: async () => {
    const res: DataResult<LoginRes> = await authAPI.getAccessToken();
    localStorage.setItem('access-token', res.data.access_token);
  },

  unAuthorize: () => {
    clearTokens();
    window.location.href = '/signin';
    throw new Error('Unauthorized');
  },

  authorize: async (): Promise<void> => {
    const tokens = getAllTokens();

    if (!tokens.accessToken || isTokenExpired(tokens.accessToken)) {
      if (!tokens.refreshToken || isTokenExpired(tokens.refreshToken)) {
        authAPI.unAuthorize();
      }

      try {
        await authAPI.refreshToken();
      } catch {
        authAPI.unAuthorize();
      }
    }
  },

  resetPassword: async (body: ResetPasswordForm): Promise<Result> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.put(`${SPRING_BASE_URL}/v1/auth/reset-password`, body, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  }
};
