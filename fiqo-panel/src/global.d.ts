export {};

declare global {
  interface Window {
    env: {
      SPRING_BASE_URL: string;
      [key: string]: string;
    };
  }
}
