 export const formatDate = (date) => {
  if(!date) return;
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
}

export const getHoursFromStr = (strHour) => {
  return strHour.split(":")[0];
}

export const datetimeToString = (d) => {
  const year = d.getFullYear();
  const month = (d.getMonth() + 1).toString().padStart(2, '0');
  const day = (d.getDate()).toString().padStart(2, '0');
  const hour = (d.getHours()).toString().padStart(2, '0');
  const minutes = (d.getMinutes()).toString().padStart(2, '0');
  return `${year}-${month}-${day}T${hour}:${minutes}:00`;
}

export const dateToString = (d) => {
  const year = d.getFullYear();
  const month = (d.getMonth() + 1).toString().padStart(2, '0');
  const day = (d.getDate()).toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export const timeToString = (d) => {
  const hour = (d.getHours()).toString().padStart(2, '0');
  const minutes = (d.getMinutes()).toString().padStart(2, '0');
  return `${hour}:${minutes}`;
}
