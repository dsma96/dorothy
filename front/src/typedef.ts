
export interface Member {
    phone: string,
    name: string,
    email?: string,
    password?: string,
    newPassword?: string
    id: number,
    rootUser: boolean,
    memo?: string,
    createDate?: string
}

export interface MemberStat{
    id: number,
    name: string,
    reservationCount: number,
    createDate: string,
    lastDate: string,
    firstDate: string,
    memo?:string
}

export interface ReserveTimeEvent {
    id: number,
    title: string,
    start: Date,
    end: Date
}

export interface HairService {
    serviceId: number,
    name: string,
    mandatory: boolean,
    idx: number,
    use: boolean,
    visible: boolean
    defaultValue: boolean,
    serviceTime: number,
    price: number,
    selected?:boolean
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
    userId: number
}

export interface Stamp{
    serviceDate:string,
    stampCount: number,
    userId: number,
}

export interface OffDay{
    offDay: string,
    designer: number
}