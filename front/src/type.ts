
export interface Member {
    phone: string,
    name: string,
    email?: string
}

export interface ReserveTimeEvent {
    id: number,
    title: string,
    start: Date,
    end: Date
}

export interface HairService{
    serviceId: number,
    name: string,
    mandatory: boolean,
    idx: number,
    use: boolean
}

export interface Reservation{
    reservationId: number,
    userName: string,
    phone: string,
    startDate: string,
    endDate?: string,
    createDate?: string,
    services: HairService[],
    status: string,
    memo: string
}