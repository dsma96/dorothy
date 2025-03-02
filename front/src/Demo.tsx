import * as React from 'react';
import { AppProvider } from '@toolpad/core/AppProvider';
import {
    AuthResponse,
    SignInPage,
    type AuthProvider,
} from '@toolpad/core/SignInPage';
import { useTheme } from '@mui/material/styles';


// preview-start
const providers = [
    { id: 'google', name: 'Google' },
];
// preview-end

const signIn: (provider: AuthProvider) => void | Promise<AuthResponse> = async (
    provider,
) => {
    // preview-start
    const promise = new Promise<AuthResponse>((resolve) => {
        setTimeout(() => {
            console.log(`Sign in with ${provider.id}`);
            resolve({ error: 'This is a fake error' });
        }, 500);
    });
    // preview-end
    return promise;
};

export default function OAuthSignInPage() {
    const theme = useTheme();
    return (
        // preview-start
        <AppProvider theme={theme}>
            <SignInPage signIn={signIn} providers={providers} />
        </AppProvider>
        // preview-end
    );
}
