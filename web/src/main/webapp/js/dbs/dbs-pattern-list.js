$(function () {
	$('a.pattern-delete').click(function (event) {
		event.preventDefault();
		var url = $(this).attr('data-url');
		if (confirm('Jesteś pewien, że chcesz usunąć tę definicję?')) {		
			window.location = url;
		}
    });
});    
    
