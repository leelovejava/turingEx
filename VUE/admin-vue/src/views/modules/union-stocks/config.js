import { isAuth } from '@/utils'
export const getStockName = data => {
    let name = "US-stocks";
    if(isAuth("union-stocks:us")){
        name = "US-stocks";
    }
    else if(isAuth("union-stocks:hk")){
        name = "HK-stocks";
    }
    else if(isAuth("union-stocks:tw")){
        name = "TW-stocks";
    }
    else if(isAuth("union-stocks:a")){
        name = "A-stocks";
    }
    else if(isAuth("union-stocks:jp")){
        name = "JP-stocks";
    }
    else if(isAuth("union-stocks:india")){
        name = "INDIA-stocks";
    }
    else if(isAuth("union-stocks:uk")){
        name = "UK-stocks";
    }
    return name;
}
