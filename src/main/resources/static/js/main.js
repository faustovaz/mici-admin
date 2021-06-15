import {CalendarTTS} from './CalendarTTS.js'

(function(idElementWrapper){
    calendar = new CalendarTTS(idElementWrapper, {
        listOfDays: [
                    'Domingo',
                    'Segunda',
                    'Terça',
                    'Quarta',
                    'Quinta',
                    'Sexta',
                    'Sábado'
        ],
        listOfMonths: [
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
                    'Dezembro']
    });
    calendar.setDatesAsScheduled([
            new Date(2021, 5, 1, 0, 0, 0, 0),
            new Date(2021, 5, 3, 0, 0, 0, 0),
            new Date(2021, 5, 4, 0, 0, 0, 0)]);
})('#calendarWrapper')
