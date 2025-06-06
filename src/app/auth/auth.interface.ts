export interface TokenResponse {
  access_token: string;
  data: {
    user: {
      userType: string;
      email: string;
      firstName: string;
      lastName: string;
      locked: boolean;
      verified: boolean;
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
  locked: boolean;
  verified: boolean;
  avatarPath?: string;
}
