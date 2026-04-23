export const currentUnit = (data) => {
  const unitList = {
    'indices': 'USD',
    'cryptos': 'USDT',
    'forex': 'USD',
    'US-stocks': 'USD',
    'HK-stocks': 'HKD',
    'TW-stocks': 'TWD',
    'JP-stocks': 'JPY',
    'A-stocks': 'CNY',
    'UK-stocks': 'GBP',
    'DE-stocks': 'EUR',
    'BZ-stocks': 'BRL',
    'INDIA-stocks': 'INR',
  } 
  return unitList[data] || 'USD'
}