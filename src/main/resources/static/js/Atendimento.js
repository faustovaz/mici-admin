class Atendimentos {

  constructor(containerId) {
    this.availableServices = [];
    this.fecthAvailableServices();
    this.container = document.querySelector(containerId);
    this.btnAddServico = document.querySelector("#btn-add-servico");
    const btnAddServico = document.querySelector("#btn-add-servico");
    btnAddServico.addEventListener('click', s => this.addNewService());
    this.bindEvents();
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


  bindEvents() {
      this.addEventListenerToRemoveServiceButtons("container-1");
      this.addEventListenerToSelectOptions("container-1");
      this.addEventListenerToChangePriceValues("container-1");
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
          name="tipo-servico-${id}"
          data-action="select-servico"
          data-holder="${serviceContainerId}"
          id="tipo_servico_${id}" aria-label="Default select example">
            <option selected>Selecione uma opção</option>
            ${opcoes}
        </select>
      </div>
      <div class="col-3">
        <label for="nome" class="form-label">Valor padrão</label>
        <div class="input-group">
            <span class="input-group-text" id="price-text">R$</span>
            <input type="text" class="form-control" disabled
              name="vp-${id}" id="vp-${id}" data-holder="${serviceContainerId}"
              required pattern="^[0-9]+[.]?[0-9]+$">
        </div>
      </div>
      <div class="col-3">
        <label for="nome" class="form-label">Valor aplicado</label>
        <div class="input-group">
            <span class="input-group-text" id="price-text">R$</span>
            <input type="number" class="form-control" step="0.01"
              name="va_${id}" id="va_${id}" data-holder="${serviceContainerId}"
              data-action="valor-aplicado"
              required pattern="^[0-9]+[.]?[0-9]+$">
        </div>
      </div>
      <div class="col-1 d-flex align-items-end">
        <button class="btn btn-outline-danger"
          data-action="remove-servico" data-holder="${serviceContainerId}">
          <i class="fa fa-remove"></i>
        </button>
      </div>
    </div>`);
    this.addEventListenerToRemoveServiceButtons(serviceContainerId);
    this.addEventListenerToSelectOptions(serviceContainerId);
    this.addEventListenerToChangePriceValues(serviceContainerId);
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
    const inputs = document.querySelectorAll('[data-action="valor-aplicado"]');
    const totalAplicado = document.querySelector("#total-atendimento");
    const total = Array.from(inputs)
      .map(input => parseFloat(input.value))
      .filter(e => !isNaN(e))
      .reduce((i, sum) => i + sum);
    const totalF = new Intl.NumberFormat(
      'pt-Br',
      {style: 'currency', currency: "BRL"}).format(total)
    totalAplicado.innerHTML = totalF;
  }

}

new Atendimentos("#services-container");
