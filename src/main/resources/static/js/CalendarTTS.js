export class CalendarTTS {

    constructor(element, options) {
        this.element = element;
        this.options = options;
        this.scheduledDays = this.options.scheduledDays;
        this.currentDate = this._setHoursToZero(new Date());
        this.today = this._setHoursToZero(new Date());
        this.currentDay = this.currentDate.getDay();
        this.currentMonth = this.currentDate.getMonth();
        this.currentYear = this.currentDate.getFullYear();
        this.selectedValue = this._setHoursToZero(new Date());

        this.domElement = document.querySelector(this.element);

        this.calendarWrapper = this.createDomElement('div', 'calendar-wrapper');
        this.calendarElement = this.createDomElement('div', 'calendar');
        this.calendarHeader = this.createDomElement('header', 'calendar-header');
        this.calendarHeaderTitle = this.createDomElement('h4', 'calendar-header-title');
        this.navigationWrapper = this.createDomElement('section', 'navigation-wrapper');
        this.previousMonthArrow = this.createDomElement('button', 'previous-month');
        this.nextMonthArrow = this.createDomElement('button', 'next-month');
        this.calendarGridDays = this.createDomElement('section', 'calendar-days');
        this.calendarGrid = this.createDomElement('section', 'calendar-grid');

        this.calendarDayElementType = 'time';

        this.insertHeaderIntoCalendarWrapper();
        this.insertCalendarGridDaysHeader();
        this.insertDaysIntoGrid();
        this.insertNavigationButton();
        this.insertCalendarIntoWrapper();
        this.domElement.appendChild(this.calendarWrapper);
    }



    _setHoursToZero(date) {
        if(!date) return;
        date.setHours(0, 0 ,0, 0);
        return date;
    }



    createDomElement(htmlElementName, elementId) {
        let element = document.createElement(htmlElementName);
        element.id = elementId;
        return element;
    }



    insertHeaderIntoCalendarWrapper() {
        let month = this.getListOfMonthsAsText()[this.currentMonth];
        this.calendarHeaderTitle.textContent = `${month} - ${this.currentYear}`;
        this.calendarHeader.appendChild(this.calendarHeaderTitle);
        this.calendarWrapper.appendChild(this.calendarHeader);
    }



    insertCalendarGridDaysHeader() {
        this.getListOfDaysAsText().forEach( day => {
            let dayElement = document.createElement('span');
            dayElement.textContent = day;
            this.calendarGridDays.appendChild(dayElement);
        });

        this.calendarElement.appendChild(this.calendarGridDays);
    }



    insertDaysIntoGrid() {
        this.calendarGrid.innerHTML = '';
        let arrayOfDays = this.getDaysInMonth(
                                  this.currentMonth, this.currentYear);

        if (arrayOfDays[0].getDay() > 0) {
            // Fill array in the left with false value
            arrayOfDays = Array(arrayOfDays[0].getDay())
                          .fill(false, 0)
                          .concat(arrayOfDays);
        }

        arrayOfDays.forEach(dayDate => {
            let dateElement = document.createElement(
                                dayDate ? this.calendarDayElementType : 'span');
            // Mark today's day as selected
            if(dayDate && dayDate.getTime() === this.today.getTime()) {
                this.activeElement = dateElement;
                this.activeElement.classList.add('selected');
            }

            if (dayDate && this.scheduledDays){
                let scheduledTimes = this.scheduledDays.map(d => d.getTime());
                if (scheduledTimes.includes(dayDate.getTime())) {
                    dateElement.classList.add('scheduled');
                }
            }

            dateElement.tabIndex = 0;
            dateElement.value = dayDate;

            dateElement.textContent = dayDate ? dayDate.getDate() : '';
            this.calendarGrid.appendChild(dateElement);
        });

        this.calendarElement.appendChild(this.calendarGrid);
    }



    insertNavigationButton() {
        var arrowSvg = "<svg enable-background='new 0 0 386.257 386.257' \
                                            viewBox='0 0 386.257 386.257' \
                                       xmlns='http://www.w3.org/2000/svg'> \
                   <path d='m0 96.879 193.129 192.5 193.128-192.5z'/></svg>";

        this.previousMonthArrow.innerHTML = arrowSvg;
        this.nextMonthArrow.innerHTML = arrowSvg;
        this.previousMonthArrow.setAttribute('aria-label', 'Go to previous month');
        this.nextMonthArrow.setAttribute('aria-label', 'Go to next month');
        this.navigationWrapper.appendChild(this.previousMonthArrow);
        this.navigationWrapper.appendChild(this.nextMonthArrow);
        this.calendarElement.appendChild(this.navigationWrapper);

        this.navigationWrapper.addEventListener('click', (evt) => {
            if (evt.target.closest('#' + this.previousMonthArrow.id)) {
                if (this.currentMonth === 0) {
                    this.currentMonth = 11;
                    this.currentYear = this.currentYear - 1;
                }
                else {
                    this.currentMonth =  this.currentMonth - 1;
                }
            }
            if (evt.target.closest('#' + this.nextMonthArrow.id)) {
                if (this.currentMonth === 11) {
                    this.currentMonth = 0;
                    this.currentYear =  this.currentYear + 1;
                }
                else {
                    this.currentMonth = this.currentMonth + 1;
                }
            }
            this.updateCalendar();
        }, false);
    }



    updateCalendar() {
        this.currentDate = new Date(this.currentYear, this.currentMonth, 1, 0, 0, 0);
        this.currentDay = this.currentDate.getDay();
        this.currentMonth = this.currentDate.getMonth();
        this.currentYear = this.currentDate.getFullYear();

        window.requestAnimationFrame((tmp) => {
            let month = this.getListOfMonthsAsText()[this.currentMonth];
            this.calendarHeaderTitle.textContent = `${month} - ${this.currentYear}`;
            this.insertDaysIntoGrid();
        });
    }



    setDatesAsScheduled(dates) {
        if (!dates) return;
        this.scheduledDays = dates.map(d => this._setHoursToZero(d));
        this.updateCalendar();
    }



    getDaysInMonth(month, year) {
        let date = new Date(year, month, 1, 0, 0, 0, 0);
        let days = [];
        while (date.getMonth() === month) {
            days.push(new Date(date));
            date.setDate(date.getDate() + 1);
        }
        return days;
    }



    insertCalendarIntoWrapper() {
        this.calendarWrapper.appendChild(this.calendarElement);

        this.calendarGrid.addEventListener('click', (evt) => {
            let allSelected = Array.from(document.querySelectorAll('.selected'));
            allSelected.forEach(e => e.classList.remove('selected'));
            evt.target.classList.add('selected');
            this.selectedValue = evt.target.value;
            this.onValueChange(this.callback);
        }, false);
    }



    onValueChange(callback) {
        if (this.callback)
            return this.callback(this.selectedValue);
        this.callback = callback;
    }



    getListOfDaysAsText() {
        return this.options.listOfDays ? this.options.listOfDays : [
                    'Sunday',
                    'Monday',
                    'Tuesday',
                    'Wednesday',
                    'Thursday',
                    'Friday',
                    'Saturday'
        ];
    }



    getListOfMonthsAsText() {
        return this.options.listOfMonths ? this.options.listOfMonths : [
                    'January',
                    'February',
                    'March',
                    'April',
                    'May',
                    'June',
                    'July',
                    'August',
                    'September',
                    'October',
                    'November',
                    'December'
        ];
    }
}
