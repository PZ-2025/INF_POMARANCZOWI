export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  data: {
    user: {
      userType: string;
      email: string;
      firstName: string;
      lastName: string;
    }
  }
}
