class Atendimentos {

  constructor(containerId) {
    this.availableServices = [];
    this.fecthAvailableServices();
    this.container = document.querySelector(containerId);
    this.bindEvents("container-0", true);
  }


  fecthAvailableServices() {
      const req = fetch('/servicos/data.json');
      req.then(response => {
        if(response.ok) {
          return response.json()
        }
        throw new Error("Error fetching scheduled dates");
      })
      .then(data => this.availableServices = data)
      .catch(err => errorHandler(err));
  }


  bindEvents(containerId, rebindAll) {
    this.addEventListenerToRemoveServiceButtons(containerId);
    this.addEventListenerToSelectOptions(containerId);
    this.addEventListenerToChangePriceValues(containerId);
    if(rebindAll) {
      document.querySelector("#btn-add-servico").addEventListener('click', s => this.addNewService());
      document.querySelectorAll("input[name=seraCobrado]").forEach(e => {
        e.addEventListener('click', (evt) => {
          this._showHideSectionDadosPagamentos(evt.target);
        });
      });
    }
  }


  fetchSelectOptions(){
    return this.availableServices.map(data =>
      `<option value=${data.id}>${data.nome}</option>`
    ).reduce((e, res) => e + res);
  }


  addNewService() {
    const id = this._getLastUsedId() + 1;
    const opcoes = this.fetchSelectOptions();
    const serviceContainerId = `container-${id}`;
    this.container.insertAdjacentHTML("beforeEnd", `
      <div class="mb-3 row" data-container="${serviceContainerId}">
      <div class="col-5">
        <label for="nome" class="form-label">Serviço</label>
        <select class="form-select"
          name="servicos[${id}].tipoServico"
          id="servicos[${id}].tipoServico"
          data-action="select-servico"
          data-holder="${serviceContainerId}"
          aria-label="Default select example">
            <option selected>Selecione uma opção</option>
            ${opcoes}
        </select>
      </div>
      <div class="col-3">
        <label for="nome" class="form-label">Valor padrão</label>
        <div class="input-group">
            <span class="input-group-text" id="price-text">R$</span>
            <input type="text" 
        		class="form-control" disabled
              	name="servicos[${id}].valorPadrao" 
              	id="servicos[${id}].valorPadrao" 
              	data-type="valor-padrao"
              	data-holder="${serviceContainerId}"
              	required 
              	pattern="^[0-9]+[.]?[0-9]+$">
        </div>
      </div>
      <div class="col-3">
        <label for="nome" class="form-label">Valor aplicado</label>
        <div class="input-group">
            <span class="input-group-text" id="price-text">R$</span>
            <input type="number" 
            	class="form-control" 
            	step="0.01"
              	name="servicos[${id}].valorAplicado" 
              	id="servicos[${id}].valorAplicado" 
              	data-type="valor-aplicado"
              	data-holder="${serviceContainerId}"
              	data-action="valor-aplicado"
              	required 
              	pattern="^[0-9]+[.]?[0-9]+$">
        </div>
      </div>
      <div class="col-1 d-flex align-items-end">
        <button class="btn btn-outline-danger"
          data-action="remove-servico" data-holder="${serviceContainerId}">
          <i class="fa fa-remove"></i>
        </button>
      </div>
    </div>`);
	this.bindEvents(serviceContainerId, false);
  }


  _getLastUsedId() {
    const services = this.container.querySelectorAll("[data-container]");
    return parseInt(Array.from(services)
      .map(e => e.getAttribute("data-container").split('-').pop()).sort().pop());
  }


  addEventListenerToRemoveServiceButtons(serviceContainerId){
    const selector = `button[data-holder=${serviceContainerId}]`;
    const btn = document.querySelector(selector);
    btn.addEventListener('click', e => {
      const element = e.target.tagName === 'I' ? e.target.parentNode : e.target;
      const data = element.getAttribute("data-holder");
      this.removeService(data);
    });
  }


  removeService(dataServiceId) {
    const allServices = this.container.querySelectorAll("[data-container]");
    if(allServices.length > 1) {
      const elemToBeRemoved = Array
        .from(allServices)
        .filter(e => e.getAttribute("data-container") === dataServiceId)
        .pop();
      if(elemToBeRemoved) {
        this.container.removeChild(elemToBeRemoved);
      }
    }
    this.updateServicosIds();
    this.updateTotalAtendimento();
  }


  addEventListenerToSelectOptions(serviceContainerId) {
    const selector = `select[data-holder=${serviceContainerId}]`;
    const select = document.querySelector(selector);
    select.addEventListener('change', e => {
      const serviceId = e.target.value;
      const inputs = document.querySelectorAll(`input[data-holder=${serviceContainerId}]`);
      inputs.forEach(input => {
        const selectedService = this.availableServices.filter(s => s.id == serviceId).pop();
        input.value = selectedService.preco;
      });
      this.updateTotalAtendimento();
    });
  }


  addEventListenerToChangePriceValues(serviceContainerId) {
    const inputs = document.querySelectorAll(`input[data-holder=${serviceContainerId}]`);
    inputs.forEach(input => {
      input.addEventListener('change', e => this.updateTotalAtendimento());
    })
  }


  updateTotalAtendimento() {
	const total = this._sumTotalAtendimento();
    const totalAplicado = document.querySelector("#total-atendimento");
    const totalF = new Intl.NumberFormat(
      'pt-Br',
      {style: 'currency', currency: "BRL"}).format(total)
    totalAplicado.innerHTML = totalF;
  }
  
  
  _sumTotalAtendimento(){
    const inputs = document.querySelectorAll('[data-action="valor-aplicado"]');
    const values = Array.from(inputs)
      .map(input => parseFloat(input.value))
      .filter(e => !isNaN(e));
    if(values.length > 0)
    	return values.reduce((i, sum) => i + sum);
    return 0;
  }

  
  updateServicosIds() {
  	let id = 0;
  	const servicosElements = Array.from(document.querySelectorAll("[data-container]"));
  	servicosElements.forEach(s => {
  		s.setAttribute('data-container', `container-${id}`);
  		let select = s.querySelector('select');
  		let valorAplicado = s.querySelector('[data-type=valor-aplicado]');
  		let valorPadrao = s.querySelector('[data-type=valor-padrao]');
  		select.id =   `servicos[${id}].tipoServico`;
  		select.name = `servicos[${id}].tipoServico`;
  		valorAplicado.id =   `servicos[${id}].valorAplicado`;
  		valorAplicado.name = `servicos[${id}].valorAplicado`;
  		valorPadrao.id =   `servicos[${id}].valorPadrao`;
  		valorPadrao.name = `servicos[${id}].valorPadrao`;
  		id = id + 1;
  	});
  }
    
  
  _showHideSectionDadosPagamentos(seraCobradoRadioButton) {
    const sectionPgto = document.querySelector("#dados-pagamento");
    if(seraCobradoRadioButton.value === 'false') { // Não será cobrado
      sectionPgto.classList.remove('d-block');
      sectionPgto.classList.add('d-none');
    } else {
      sectionPgto.classList.remove('d-none');
      sectionPgto.classList.add('d-block');
    }
  }
  
}

new Atendimentos("#services-container");
