import axios from 'axios';
import { DataResult } from '../model/result/DataResult';
import { SPRING_BASE_URL } from '../utils/utils';
import { FileInfo } from '../model/file/FileInfo';
import { getAccessToken } from './storage-manager';
import { Result } from '../model/result/Result';
import { authAPI } from './auth-service';

export const fileAPI = {
  getAllFiles: async (path: string = ''): Promise<DataResult<FileInfo[]>> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.get(`${SPRING_BASE_URL}/v1/files?path=${path}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  downloadFile: async (path: string): Promise<Blob> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.get(`${SPRING_BASE_URL}/v1/files/download?path=${path}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      },

      responseType: 'arraybuffer'
    });

    return new Blob([response.data]);
  },

  deleteFile: async (path: string, recursive: boolean): Promise<Result> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.delete(`${SPRING_BASE_URL}/v1/files?path=${path}&recursive=${recursive}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  uploadFile: async (path: string, file: File): Promise<Result> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const formData = new FormData();
    formData.append('file', file);

    const response = await axios.post(`${SPRING_BASE_URL}/v1/files?path=${encodeURIComponent(path)}`, file, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': file.type || 'application/octet-stream'
      }
    });
    return response.data;
  },

  pasteFile: async (sourcePath: string, targetPath: string, action: string): Promise<Result> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();

    const response = await axios.post(
      `${SPRING_BASE_URL}/v1/files/paste?sourcePath=${sourcePath}&targetPath=${targetPath}&action=${action}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${accessToken}`,
          'Content-Type': 'application/json'
        }
      }
    );
    return response.data;
  }
};
