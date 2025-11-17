declare module 'event-source-polyfill' {
  export class EventSourcePolyfill {
    constructor(
      url: string,
      options?: {
        headers?: Record<string, string>;
        heartbeatTimeout?: number;
        [key: string]: unknown;
      }
    );

    addEventListener(type: string, listener: (event: MessageEvent) => void): void;
    removeEventListener(type: string, listener: (event: MessageEvent) => void): void;

    close(): void;

    onerror: ((err: unknown) => void) | null;
    onopen: ((event: Event) => void) | null;
    onmessage: ((event: MessageEvent) => void) | null;
  }

  export const NativeEventSource: typeof EventSource | undefined;
}
