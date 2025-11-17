import axios from 'axios';
import { DataResult } from '../model/result/DataResult';
import { getAccessToken } from './storage-manager';
import { SPRING_BASE_URL } from '../utils/utils';
import { authAPI } from './auth-service';
import { Usage } from '../model/usage/Usage';

export const usageAPI = {
  getUsages: async (year: number): Promise<DataResult<Usage[]>> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.get(`${SPRING_BASE_URL}/v1/daily-usages?year=${year}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  }
};
