import { DataResult } from '../model/result/DataResult';
import { UserInfo } from '../model/user/UserInfo';
import { SPRING_BASE_URL } from '../utils/utils';

import axios from 'axios';
import { getAccessToken } from './storage-manager';
import { authAPI } from './auth-service';
import { ProfileForm } from '../model/user/ProfileForm';
import { Result } from '../model/result/Result';

export const userAPI = {
  getProfile: async (): Promise<DataResult<UserInfo>> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.get<DataResult<UserInfo>>(`${SPRING_BASE_URL}/v1/users/profile`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  updateProfile: async (body: ProfileForm): Promise<Result> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.put(`${SPRING_BASE_URL}/v1/users/profile`, body, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  }
};
