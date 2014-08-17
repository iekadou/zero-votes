$(document).ready(function() {
    toastr.options = {
        "closeButton": true
    };

	var messages_displayed = [];
	$('.notification-fatal').each(function() {
		if (messages_displayed.indexOf('fatal_'+$(this).html()) == -1) {
			toastr.error($(this).html());
			messages_displayed.push('fatal_'+$(this).html());
		}
	});
	$('.notification-error').each(function() {
		if (messages_displayed.indexOf('error_'+$(this).html()) == -1) {
			toastr.error($(this).html());
			messages_displayed.push('error_'+$(this).html());
		}
	});
	$('.notification-warning').each(function() {
		if (messages_displayed.indexOf('warning_'+$(this).html()) == -1) {
			toastr.warning($(this).html());
			messages_displayed.push('warning_'+$(this).html());
		}
	});
	$('.notification-info').each(function() {
		if (messages_displayed.indexOf('info_'+$(this).html()) == -1) {
			toastr.info($(this).html());
			messages_displayed.push('info_'+$(this).html());
		}
	});

    // --------------------------

    var current_language = $('html').attr('lang') || "de";

    var preview_text, edit_text, delete_text, publish_text;

    switch (current_language) {
        case "en":
            preview_text = "Preview";
            edit_text = "Edit";
            delete_text = "Delete";
            publish_text = "Publish";
            participants_text = "Participants";
            questions_text = "Questions";
            break;
        case "de":
        default:
            preview_text = " Vorschau";
            edit_text = "Editieren";
            delete_text = "Löschen";
            publish_text = "Veröffentlichen";
            participants_text = "Teilnehmer";
            questions_text = "Fragen";
            break;
    }

    $('.zvotes-preview-btn').parent().tooltip({
        'placement': 'bottom',
        'title': preview_text
    });

    $('.zvotes-edit-btn').parent().tooltip({
        'placement': 'bottom',
        'title': edit_text
    });

    $('.zvotes-delete-btn').parent().tooltip({
        'placement': 'bottom',
        'title': delete_text
    });

    $('.zvotes-publish-btn').parent().tooltip({
        'placement': 'bottom',
        'title': publish_text
    });

    $('.zvotes-participants-btn').parent().tooltip({
        'placement': 'bottom',
        'title': participants_text
    });

    $('.zvotes-questions-btn').parent().tooltip({
        'placement': 'bottom',
        'title': questions_text
    });
	
});
