export interface Session {
  token: string;
  expiration: number;
  expirationDate: Date;
  refreshToken: string;
}
