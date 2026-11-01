import {Injectable,inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Product,Order,Status,Dashboard} from './models';
@Injectable({providedIn:'root'})
export class ApiService {
 private http=inject(HttpClient);
 products(){return this.http.get<Product[]>('/api/products');}
 orders(){return this.http.get<Order[]>('/api/orders');}
 dashboard(){return this.http.get<Dashboard>('/api/dashboard');}
 restock(id:number,quantity:number){return this.http.post<Product>('/api/products/'+id+'/restock',{quantity});}
 create(retailer:string,items:{productId:number;quantity:number}[],key:string){return this.http.post<Order>('/api/orders',{retailer,items},{headers:{'Idempotency-Key':key}});}
 status(id:string,status:Status){return this.http.patch<Order>('/api/orders/'+id+'/status',{status});}
}
