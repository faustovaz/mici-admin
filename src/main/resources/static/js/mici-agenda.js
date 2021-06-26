import {CalendarTTS} from './CalendarTTS.js'

(function(idElementWrapper){
	const listOfDays = [
        'Domingo',
        'Segunda',
        'Terça',
        'Quarta',
        'Quinta',
        'Sexta',
        'Sábado'
	];
	
	const listOfMonths = [
        'Janeiro',
        'Fevereiro',
        'Março',
        'Abril',
        'Maio',
        'Junho',
        'Julho',
        'Agosto',
        'Setembro',
        'Outubro',
        'Novembro',
        'Dezembro'
	];
	
    const miciCalendar = new CalendarTTS(idElementWrapper, {
        listOfDays: listOfDays,
        listOfMonths: listOfMonths
    });

	miciCalendar.onValueChange(selectedDate => {
		if(selectedDate) {
			const modalTitle = document.querySelector("#modal-title-id");
			const weekDay = listOfDays[selectedDate.getDay()];
			const bsModal = new bootstrap.Modal(document.querySelector('#schedule-date-modal'));
			modalTitle.innerHTML = `${weekDay} - ${formatDate(selectedDate)}`;
			bsModal.show();
		}	
	});
	
	const btnAgendar = document.querySelector("#btn-agendar");
	btnAgendar.addEventListener('click', () => {
		const selectedDate = miciCalendar.selectedValue;
		const horario = document.querySelector("#horario").value
		const cliente = document.querySelector("#cliente").value
		const lembrete = document.querySelector("#lembrete").value
	
		selectedDate.setHours(horario.split(":")[0]);
		selectedDate.setMinutes(horario.split(":")[1]);
		
		const postData = {dataHoraAgendamento: selectedDate.toISOString(), cliente: cliente, lembrete: lembrete};
		
		fetch(
			'/agenda/agendar', 
			{
				method: 'POST', 
				body: JSON.stringify(postData),
			    headers: {"Content-Type": "application/json"}
			}
		).then((r) => console.log(r)).catch((err) => console.log(err));
		
	});
	
	

	function formatDate(date) {
		if(!date) return;
		const day = date.getDate().toString().padStart(2, '0');
		const month = (date.getMonth() + 1).toString().padStart(2, '0');
		const year = date.getFullYear();
		return `${day}/${month}/${year}`;
	}
	
	function getHoursFromStr(strHour) {
		return strHour.split(":")[0];
	}
		
})('#calendarWrapper')
