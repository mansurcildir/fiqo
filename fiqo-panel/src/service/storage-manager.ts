const ACCESS_TOKEN_KEY = 'access-token';
const REFRESH_TOKEN_KEY = 'refresh-token';
const TOKEN_PREFIX = 'Bearer ';

export const setTokens = (accessToken: string, refreshToken: string) => {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
};

export const getAccessToken = () => {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
};

export const getRefreshToken = () => {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
};

export const getAllTokens = () => {
  return {
    accessToken: getAccessToken(),
    refreshToken: getRefreshToken()
  };
};

export const checkTokensExist = () => {
  return !!getAccessToken() && !!getRefreshToken();
};

export const getBearerAccessTokenWithHeaderAttribute = () => {
  return {
    Authorization: `${TOKEN_PREFIX}${getAccessToken()}`,
    'Content-Type': 'application/json'
  };
};

export const getRefreshTokenWithHeaderAttribute = () => {
  return {
    'Refresh-Token': getRefreshToken()!,
    'Content-Type': 'application/json'
  };
};

export const getBearerAccessToken = () => {
  const accessToken = getAccessToken();
  if (!accessToken) {
    return null;
  }
  return `${TOKEN_PREFIX}${accessToken}`;
};

export const clearTokens = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
};
