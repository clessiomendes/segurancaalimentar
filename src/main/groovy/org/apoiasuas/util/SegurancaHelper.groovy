package org.apoiasuas.util

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
 import com.google.api.client.json.jackson.JacksonFactory
import grails.util.Environment
import org.apoiasuas.redeSocioAssistencial.ServicoSistema
import org.apoiasuas.redeSocioAssistencial.UsuarioSistema

import javax.servlet.http.HttpSession;

class SegurancaHelper {
    private static final String GOOGLE_CLIENT_ID = "192749249074-5ho7vfu5q8vbad22l25l9od2c5cgsf3i.apps.googleusercontent.com";

    public static GoogleIdToken.Payload getPayload (String tokenString) throws Exception {

        JacksonFactory jacksonFactory = new JacksonFactory();
        GoogleIdTokenVerifier googleIdTokenVerifier =
                            new GoogleIdTokenVerifier(new NetHttpTransport(), jacksonFactory);

        GoogleIdToken token = GoogleIdToken.parse(jacksonFactory, tokenString);

        if (googleIdTokenVerifier.verify(token)) {
            GoogleIdToken.Payload payload = token.getPayload();
            if (!GOOGLE_CLIENT_ID.equals(payload.getAudience())) {
                throw new IllegalArgumentException("Audience mismatch");
            } else if (!GOOGLE_CLIENT_ID.equals(payload.getAuthorizedParty())) {
                throw new IllegalArgumentException("Client ID mismatch");
            }
            return payload;
        } else {
//            throw new IllegalArgumentException("id token cannot be verified");
        }
    }

    public static void login(HttpSession session, ServicoSistema ss, UsuarioSistema us) {
        session.credencial = new Credencial(ss, us);
    }

    public static void logout(HttpSession session) {
        session.credencial = null;
    }

    public static boolean logado(HttpSession session) {
        return session.credencial != null;
    }

    public static Credencial getCredencial(HttpSession session) {
        Credencial result = session.credencial;
        if (! result && Environment.current == Environment.DEVELOPMENT)
            result = new Credencial(ServicoSistema.get(8), UsuarioSistema.get(5));
        return result;
    }
}
