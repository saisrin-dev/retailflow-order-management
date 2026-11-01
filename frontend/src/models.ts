export interface Product {id:number;sku:string;name:string;category:string;price:number;stock:number}
export type Status = 'PLACED'|'CONFIRMED'|'SHIPPED'|'DELIVERED'|'CANCELLED';
export interface Order {id:string;retailer:string;status:Status;createdAt:string;total:number;lines:{productId:number;name:string;quantity:number;unitPrice:number}[];events:{status:Status;at:string}[]}
export interface Dashboard {products:number;orders:number;lowStock:number;bookedValue:number}
