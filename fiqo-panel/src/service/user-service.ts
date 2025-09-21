import { DataResult } from '../model/result/DataResult';
import { UserInfo } from '../model/user/UserInfo';
import { SPRING_BASE_URL } from '../utils/utils';

import axios from 'axios';
import { getAccessToken } from './storage-manager';
import { authAPI } from './auth-service';

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
  }
};
