
export interface Member {
    phone: string,
    name: string,
    email?: string,
    password?: string,
    newPassword?: string
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
    use: boolean,
    visible: boolean
    defaultValue: boolean
}

export interface UploadFile{
    userFileName: string,
    url: string,
    id: number
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
    memo: string,
    requireSilence: boolean,
    editable: boolean,
    files?: UploadFile[]
}