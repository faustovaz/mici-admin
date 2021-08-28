import {CalendarTTS} from './CalendarTTS.js';
import {CalendarService} from './CalendarService.js';
import {formatDate, getHoursFromStr, datetimeToString, dateToString, timeToString} from './DateTimeUtils.js';

class CalendarManager {

  constructor(wrapperId) {
    this.btnSchedule = document.querySelector("#btn-agendar");
    this.inputScheduledTime= document.querySelector("#horario");
    this.inputCustomerName = document.querySelector("#cliente");
    this.inputReminder = document.querySelector("#lembrete");
	this.token = document.querySelector("meta[name='_csrf']").getAttribute("content");
	this.header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");  
	console.log(this.token);
	console.log(this.header);  
    this.service = new CalendarService();
    this.listOfDays = [
        'Domingo',
        'Segunda',
        'Terça',
        'Quarta',
        'Quinta',
        'Sexta',
        'Sábado'
    ];
    this.listOfMonths = [
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
    this.calendar = new CalendarTTS(wrapperId, {
      listOfDays: this.listOfDays,
      listOfMonths: this.listOfMonths
    });
    this.registerEvents();
    this.loadScheduledDates();
  }


  loadScheduledDates() {
    this.service.fetchAllScheduledDates(scheduledDates => {
      const dates = scheduledDates.map(d => new Date(d.agendamento))
      this.calendar.setDatesAsScheduled(dates);
    }, error => {
      this.showMessage("#alertErrorMsg", "Erro ao buscar dados da agenda.");
    });
  }


  registerEvents() {
    this.calendar.onValueChange(selectedDate => {
      if(selectedDate) {
        this.hideAlertMsgs();
        this.cleanAllFields();
        const modalTitle = document.querySelector("#modal-title-id");
        const weekDay = this.listOfDays[selectedDate.getDay()];
        const modalElement = document.querySelector('#schedule-date-modal');
        const bsModal = new bootstrap.Modal(modalElement);
        modalTitle.innerHTML = `${weekDay} - ${formatDate(selectedDate)}`;
        bsModal.show();
        this.updateScheduleTableFor(selectedDate);
      }});

    this.btnSchedule.addEventListener('click', (evt) => {
      try {
        this.hideAlertMsgs();
        const dataToBeSent = this.checkAndFetchFormData();
        this.service.sendScheduledDate(dataToBeSent, this.header, this.token, () => {
          this.showMessage("#modalSuccessMsg", "Dados salvos com sucesso!");
          this.cleanAllFields();
          this.setFocusOn("#cliente");
          this.loadScheduledDates();
          this.updateScheduleTableFor(new Date(dataToBeSent.agendamento));
        }, (err) =>{
          this.showMessage("#modalErrorMsg", "Erro ao enviar dadoss!");
        });
      } catch (e) {
          this.showMessage("#modalErrorMsg", e.message);
      }
    });
  }


  checkAndFetchFormData() {
    const selectedDate = this.calendar.selectedValue;
    const cliente = this.inputCustomerName.value;
    const lembrete = this.inputReminder.value;
    const horario = this.inputScheduledTime.value;
    const hora = horario.split(":")[0];
    const minutos = horario.split(":")[1];

    if(isNaN(hora) || isNaN(minutos) || cliente.trim() === "") {
      throw new Error("Os campos cliente e horário são obrigatórios!");
    }

    selectedDate.setHours(hora);
    selectedDate.setMinutes(minutos);

    return {
        agendamento: datetimeToString(selectedDate),
        cliente: cliente,
        lembrete: lembrete
    };
  }


  showMessage(errorElementId, errorMsg) {
    const errorElement = document.querySelector(errorElementId);
    errorElement.innerHTML = errorMsg;
    errorElement.classList.remove("d-none");
    errorElement.classList.add("d-block");
  }


  hideAlertMsgs() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach((alert) => {
      alert.classList.remove("d-block");
      alert.classList.add("d-none");
    });
  }


  cleanAllFields() {
    const fields = document.querySelectorAll(".form-control");
    fields.forEach(field => {
      field.value = "";
    });
  }


  setFocusOn(elementId) {
    document.querySelector(elementId).focus();
  }


  updateScheduleTableFor(date) {
    this.service.fetchAllScheduledDateByDate(
      dateToString(date), (data) => {
        const tbody = document.querySelector("#tbScheduledDates>tbody");
        tbody.replaceChildren();
        this._showTableIfDataExists(data);
        data.forEach(d => {
          const tr = `
            <tr>
              <td>${d.cliente}</td>
              <td>${timeToString(new Date(d.agendamento))}</td>
              <td class="text-end">
                <button type="button" class="btn btn-primary"
                  data-bs-toggle="tooltip"
                  data-bs-placement="left"
                  title="${d.lembrete}">
                  <i class="fa fa-file-text-o"></i>
                </button>
                <button class="btn btn-outline-danger"
                  data-action="removeSchedule"
                  data-schedule="${d.id}"
                  data-schedule-date="${dateToString(new Date(d.agendamento))}">
                  <i class="fa fa-remove"></i>
                </button>
              </td>
            </tr>`;
            tbody.innerHTML = tbody.innerHTML + tr;
        });
        this._enableToolTips();
        this._enableRemoveEventListener();
      }, (err) => {
          this.showMessage("#alertErrorMsg", "Erro ao atualizar tabela.");
          console.log(err);
      });
  }


  _enableToolTips() {
    const tooltipTriggerList = [].slice.call(
      document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
      return new bootstrap.Tooltip(tooltipTriggerEl)
    });
  }


  _enableRemoveEventListener() {
    const btns = document.querySelectorAll('[data-action="removeSchedule"]');
    btns.forEach( btn => btn.addEventListener('click', e => {
      // if clicked on the I element of the button
      const element = e.target.tagName === 'I' ? e.target.parentNode : e.target;
      const id = element.getAttribute('data-schedule');
      const date = element.getAttribute('data-schedule-date');
      this.removeSchedule(id, date);
    }));
  }


  _showTableIfDataExists(data) {
    const table = document.querySelector("#tbScheduledDates");
    if (data.length > 0) {
      table.classList.remove("d-none");
    } else {
      table.classList.add("d-none");
    }
  }


  removeSchedule(scheduledId, scheduledDate) {
    this.service.deleteScheduledDate(scheduledId, () => {
      this.updateScheduleTableFor(new Date(`${scheduledDate}T00:00:00`));
      this.loadScheduledDates();
    }, (e) => {
      this.showMessage("#modalErrorMsg", e.message);
    });
  }
}

const manager = new CalendarManager("#calendar-wrapper");
