import axios from "axios";

const API_KEY = import.meta.env.VITE_WEATHER_API_KEY;

export const getCurrentWeather = async (lat, lon) => {
  const response = await axios.get(`https://api.openweathermap.org/data/2.5/weather`, {
    params: {
      lat,
      lon,
      appid: API_KEY,
      units: "metric", // 섭씨
      lang: "kr", // 한국어
    },
  });

  return response.data;
};
