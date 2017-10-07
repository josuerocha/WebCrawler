$('#meu_formulario').submit(function(event){	
	
	event.preventDefault(); // if you want to disable the action
	
	var $form = $(this);
	var nomeValue = $form.find('input[name="nome"]').val();
	var mensagemValue = $form.find('textarea[name="mensagem"]').val();
	var assuntoValue = $form.find('input[name="assunto"]').val();
	var fromValue = $form.find('input[name="from"]').val();

	var type = $form.attr('method');
	var url = $form.attr('action');

	var postData = {
		mensagem : mensagemValue,
		nome : nomeValue,
		assunto : assuntoValue,
		from: fromValue
	}

	$('#form-area').loading();


	var posting = $.post(url,postData,function(data,status){
			if(status == 'success'){
					$('#form-area').loading('destroy');
					$("#modal-corpo").empty().append(data);
					$("#myModal").modal();
				}
			}
		);
});

