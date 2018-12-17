function atualiza() {
    $('#formFiltragem').submit();
}

function keydownNomeOuNis(event) {
    if (event.keyCode == 13)
        atualiza();
}