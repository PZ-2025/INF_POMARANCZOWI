export interface TokenResponse {
  access_token: string;
  refresh_token: string;
  data: {
    user: {
      userType: string;
      email: string;
      firstName: string;
      lastName: string;
      avatarPath: string;
      id: number;
    }
  }
}

export interface User {
  id: number;
  userType: string;
  email: string;
  firstName: string;
  lastName: string;
  avatar_path?: string;
}
