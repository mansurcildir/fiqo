import axios from 'axios';
import { DataResult } from '../model/result/DataResult';
import { SPRING_BASE_URL } from '../utils/utils';
import { FileInfo } from '../model/file/FileInfo';
import { getAccessToken } from './storage-manager';

export const fileAPI = {
  getAllFiles: async (path: string = ''): Promise<DataResult<FileInfo[]>> => {
    const accessToken = getAccessToken();
    const response = await axios.get(`${SPRING_BASE_URL}/v1/files?path=${path}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  }
};
