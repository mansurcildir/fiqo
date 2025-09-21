import { jwtDecode } from 'jwt-decode';

export interface CustomJwtPayload {
  username: string;
  exp: number;
}

export const isTokenExpired = (token: string) => {
  try {
    const decodedToken = jwtDecode(token);

    if (!decodedToken.exp) {
      return true;
    }

    return decodedToken.exp * 1000 < Date.now();
  } catch {
    return false;
  }
};
