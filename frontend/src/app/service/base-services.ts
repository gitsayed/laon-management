import { HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";






@Injectable({
    providedIn: 'root'
  })
export class BaseService {

    headers = new HttpHeaders({ 'Content-Type': 'application/json' });

    public mapToHttpParams(map: Map<string, any>) : any {
        let params = '?';
        let i= 0;
        if (map != null && map.size > 0) {
            map.forEach(( v, k) => {
                i++;
                params += k.toString() + '=' + v.toString() + (map.size==i? '':'&')
            });
        }
        return params;
    }

 
}

