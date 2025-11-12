function parseAsUTC (input) {
  // Trim to ms precision
  const normalized = input.replace(/(\.\d{3})\d+/, '$1')

  // If string already has a timezone offset or 'Z', just use it
  if (/[+-]\d{2}:\d{2}$|Z$/.test(normalized)) {
    return new Date(normalized)
  }

  return new Date(normalized + 'Z')
}

function getDateObject (inputTimestamp) {
  return new Date(inputTimestamp ? parseAsUTC(inputTimestamp) : null)
}

function formatTimestamp (inputTimestamp) {
  const dateObj = getDateObject(inputTimestamp)

  const timestamp = inputTimestamp !== null
    ? new Intl.DateTimeFormat(undefined, {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(dateObj).replace(',', '')
    : ''

  const timestampAgo = inputTimestamp !== null ? new Intl.RelativeTimeFormat().format(Math.floor((dateObj - new Date()) / 1000 / 60), 'minute') : 'Never'

  return {
    timestamp,
    timestampAgo
  }
}

function formatPacketTime (inputTimestamp) {
  const dateObj = getDateObject(inputTimestamp)
  // HH:mm:ss.SSS
  const hours = String(dateObj.getHours()).padStart(2, '0')
  const minutes = String(dateObj.getMinutes()).padStart(2, '0')
  const seconds = String(dateObj.getSeconds()).padStart(2, '0')
  const milliseconds = String(dateObj.getMilliseconds()).padStart(3, '0')
  return `${hours}:${minutes}:${seconds}.${milliseconds}`
}

export { getDateObject, formatTimestamp, formatPacketTime }
