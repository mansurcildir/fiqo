import axios from 'axios';
import { Account } from '../model/account/Account';
import { DataResult } from '../model/result/DataResult';
import { getAccessToken } from './storage-manager';
import { SPRING_BASE_URL } from '../utils/utils';

export const accountAPI = {
  getGoogleAccounts: async (): Promise<DataResult<Account[]>> => {
    const accessToken = getAccessToken();
    const response = await axios.get(`${SPRING_BASE_URL}/v1/accounts/google`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  getGithubAccounts: async (): Promise<DataResult<Account[]>> => {
    const accessToken = getAccessToken();
    const response = await axios.get(`${SPRING_BASE_URL}/v1/accounts/github`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  deleteAccountByUuid: async (accountUuid: string): Promise<void> => {
    const accessToken = getAccessToken();
    const response = await axios.delete(`${SPRING_BASE_URL}/v1/accounts/${accountUuid}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  }
};
