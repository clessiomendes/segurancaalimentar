  function handleClientLoad() {
      console.log('handleClientLoad')
    // Loads the client library and the auth2 library together for efficiency.
    // Loading the auth2 library is optional here since `gapi.client.init` function will load
    // it if not already loaded. Loading it upfront can save one network request.
//        gapi.load('client:auth2', initClient);
      gapi.load('auth2', initClient);
  }

  function initClient() {
      console.log('initClient')
    // Initialize the client with API key and People API, and initialize OAuth with an
    // OAuth 2.0 client ID and scopes (space delimited string) to request access.
      gapi.auth2.init({
//            apiKey: 'AIzaSyArhUez2Z4JniihVFcqv5G7f8za_rS6wq8',
          clientId: '192749249074-5ho7vfu5q8vbad22l25l9od2c5cgsf3i.apps.googleusercontent.com',
          hosted_domain: 'pbh.gov.br',
          scope: 'profile'
      }).then(function () {
        //somente exibir botao de login aqui
      });
  }

  function handleSignInClick(event) {
      console.log('handleSignInClick')
      gapi.auth2.getAuthInstance().signIn({
          prompt: 'select_account',
      }).then(function() {
        console.log('usuario ' + gapi.auth2.getAuthInstance().currentUser.get().getBasicProfile().getName() + ' logado.');
        var redirectUrl = '/inicio/enter';
        //using jquery to post data dynamically
        var form = $('<form action="' + redirectUrl + '" method="post">' +
                      '<input type="text" name="id_token" value="' +
                       gapi.auth2.getAuthInstance().currentUser.get().getAuthResponse().id_token + '" /> </form>');
        $('body').append(form);
        form.submit();
      });
  }

  function handleSignOutClick(event) {
      console.log('handleSignOutClick')
      gapi.auth2.getAuthInstance().signOut().then(function() {
        var redirectUrl = '/inicio/logout';
        //using jquery to post data dynamically
        var form = $('<form action="' + redirectUrl + '" method="post"/>');
        $('body').append(form);
          console.log('redirecionando para '+redirectUrl)
        form.submit();
      });
  }
