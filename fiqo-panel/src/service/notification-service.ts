import axios from 'axios';
import { DataResult } from '../model/result/DataResult';
import { getAccessToken } from './storage-manager';
import { SPRING_BASE_URL } from '../utils/utils';
import { authAPI } from './auth-service';
import { Notification } from '../model/notification/Notification';
import { EventSourcePolyfill } from 'event-source-polyfill';
import { Result } from '../model/result/Result';

export const notificationAPI = {
  getNotifications: async (): Promise<DataResult<Notification[]>> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.get(`${SPRING_BASE_URL}/v1/notifications`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  readNotification: async (notificationUuid: string): Promise<Result> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.post(`${SPRING_BASE_URL}/v1/notifications/${notificationUuid}/read`, null, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  deleteNotification: async (notificationUuid: string): Promise<Result> => {
    await authAPI.authorize();
    const accessToken = getAccessToken();
    const response = await axios.delete(`${SPRING_BASE_URL}/v1/notifications/${notificationUuid}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    return response.data;
  },

  subscribe: (onNotification: (notification: Notification) => void, onError?: (err: unknown) => void) => {
    const accessToken = getAccessToken();
    const eventSource = new EventSourcePolyfill(`${SPRING_BASE_URL}/v1/notifications/stream`, {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    });

    eventSource.addEventListener('notification', (event: MessageEvent) => {
      const data: Notification = JSON.parse(event.data);
      onNotification(data);
    });

    eventSource.onerror = (err) => {
      if (onError) onError(err);
      eventSource.close();
    };

    return eventSource;
  }
};
