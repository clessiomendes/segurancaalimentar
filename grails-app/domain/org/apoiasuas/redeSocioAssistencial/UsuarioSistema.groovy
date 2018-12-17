package org.apoiasuas.redeSocioAssistencial

/**
 * Created by cless on 19/11/2018.
 */
class UsuarioSistema {
    String nome
    String email
//    Acesso acesso
//    Date lastUpdated, dateCreated

    static mapping = {
        id generator: 'native', params: [sequence: 'sq_usuario_sistema']
        version column: 'versao'
//        lastUpdated column: 'ultima_alteracao'
//        dateCreated column: 'data_criacao'
    }

}
