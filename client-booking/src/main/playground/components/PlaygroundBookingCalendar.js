import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import Playground from '../../../connector/Playground';
import CheckButton from '../../../util/components/CheckButton';
import PlaygroundSubmitOrderModal from './PlaygroundSubmitOrderModal';
import {clearReservation, restoreReservation, saveReservation} from '../../../util/reservationSaver';
import {withMainFrameContext} from '../../../boot/frame/MainFrame'
import {env} from '../../../boot/constants';
import './PlaygroundBookingCalendar.css';

export default withMainFrameContext(withRouter(class PlaygroundBookingCalendar extends React.Component {

        static CLOSE_TITLE = 'Отмена';
        static CANCEL_TITLE = 'Сбросить выбор';
        static USER_SUBMIT_TITLE = 'Забронировать';
        static OWNER_SUBMIT_TITLE = 'Зарезервировать';

        static START_DATE_OFFSET = 0;
        static END_DATE_OFFSET = 6;

        constructor(props) {
            super(props);
            this.playgroundId = props.identifier;
            this.playgroundVersion = props.version;
            const start = PlaygroundBookingCalendar.START_DATE_OFFSET;
            const end = PlaygroundBookingCalendar.END_DATE_OFFSET;
            this.ownersURLs = null;
            this.userURL = null;
            this.timeFrame = {
                offset: {
                    start: start,
                    end: end
                },
                date: {
                    start: PlaygroundBookingCalendar.normalNow(start),
                    end: PlaygroundBookingCalendar.normalNow(end)
                }
            };
            this.state = {
                price: null,
                freed: null,
                tested: null,
                contact: null,
                schedule: null,
                dateList: null,
                timeList: null,
                halfHourAvailable: null,
                fullHourRequired: null,
                reservation: [],
                access: false,
                owner: false
            };
        }

        static normalNow(days) {
            return this.normalizeDate(this.now(days));
        }

        static normalizeDate(datetime) {
            let day = datetime.getDate();
            let month = datetime.getMonth();
            return datetime.getFullYear() + '-' + ((++month < 10) ? ('0' + month) : (month)) + '-' + ((day < 10) ? ('0' + day) : (day));
        }

        static normalizeTime(datetime) {
            let hours = datetime.getHours();
            let minutes = datetime.getMinutes();
            return ((hours < 10) ? ('0' + hours) : (hours)) + ':' + ((minutes < 10) ? ('0' + minutes) : (minutes));
        }

        static now(days) {
            const today = new Date();
            return ((days != null) && (days > 0))
                ? new Date(today.getFullYear(), today.getMonth(), (today.getDate() + days))
                : today;
        }

        componentDidMount() {
            this.calculateAccess(
                this.props.mainframe.principal
            );
            this.restore(
                this.playgroundId,
                this.playgroundVersion
            );
            this.query(
                this.playgroundId,
                this.playgroundVersion,
                this.timeFrame.date.start,
                this.timeFrame.date.end
            );
        }

        componentWillReceiveProps(nextProps) {
            this.calculateAccess(
                nextProps.mainframe.principal
            );
        }

        calculateAccess(principal) {
            if (principal) {
                this.setState({access: !(principal.roles.indexOf(env.ROLE.USER) < 0)});
                this.calculateAuthority(principal.userURL, null);
            }
        }

        calculateAuthority(userURL, ownersURLs) {
            if (userURL) this.userURL = userURL;
            if (ownersURLs) this.ownersURLs = ownersURLs;
            const user = this.userURL;
            const owners = this.ownersURLs;
            this.setState({
                owner: (((user != null) && (owners != null)) && !(owners.indexOf(user) < 0))
            });
        }

        handleOffset(event) {
            let btn = event.target.id;
            if ((btn == null) || (btn === '')) btn = event.target.parentNode.id;
            if (btn === 'btn-next') {
                this.updateGrid(1);
            } else if ((btn === 'btn-prev') && (this.timeFrame.offset.start > 0)) {
                this.updateGrid(-1);
            }
        }

        updateGrid(offset) {
            if (offset !== 0) {
                const start = this.timeFrame.offset.start + offset;
                const end = this.timeFrame.offset.end + offset;
                this.timeFrame.offset.start = start;
                this.timeFrame.offset.end = end;
                this.timeFrame.date.start = PlaygroundBookingCalendar.normalNow(start);
                this.timeFrame.date.end = PlaygroundBookingCalendar.normalNow(end);
                this.query(
                    this.playgroundId,
                    this.playgroundVersion,
                    this.timeFrame.date.start,
                    this.timeFrame.date.end
                );
            }
        }

        restore(id, version) {
            Playground.doCheck(id, version, restoreReservation(id, version))
                .then(reservations => {
                    console.debug('PlaygroundBookingCalendar', 'restore', reservations);
                    this.setState({reservation: reservations});
                })
                .catch(error => {
                    console.error('PlaygroundBookingCalendar', 'restore', 'failed');
                });
        }

        query(id, version, from, to) {
            Playground.getBoard(id, from, to)
                .then(data => {
                    console.debug('PlaygroundBookingCalendar', 'query', 'success');
                    const playground = data.playground;
                    this.calculateAuthority(null, playground.ownersURLs);
                    const dateList = [];
                    const timeList = [];
                    const price = playground.halfHourAvailable ? Math.floor(playground.price / 2) : playground.price;
                    const array = Object.entries(data.grid.schedule);
                    Object.entries(array[0][1]).forEach(item => {
                        timeList.push(item[0])
                    });
                    array.forEach((value, index, array) => {
                        array[index][1] = new Map(Object.entries(value[1]));
                        const date = value[0];
                        const dateClass = new Date(date);
                        dateList.push({
                            month: env.MONTH_NAMES[dateClass.getMonth()],
                            dayOfWeek: env.DAYS_OF_WEEK_NAMES[dateClass.getDay()],
                            date: date
                        });
                    });
                    const schedule = new Map(array);
                    this.setState({
                        price: price,
                        dateList: dateList,
                        timeList: timeList,
                        schedule: schedule,
                        halfHourAvailable: playground.halfHourAvailable,
                        fullHourRequired: playground.fullHourRequired,
                        contact: playground.phone,
                        freed: playground.isFreed,
                        tested: playground.isTested,
                    });
                })
                .catch(error => {
                    console.debug('PlaygroundBookingCalendar', 'query', 'failed');
                });
        }

        updateReservation(event) {
            const value = event.target.value;
            const checked = event.target.checked;
            const buildByOffset = minuteDiff => {
                const valueDatetime = new Date(value);
                const datetime = new Date(new Date(value).setMinutes(valueDatetime.getMinutes() + minuteDiff));
                const date = PlaygroundBookingCalendar.normalizeDate(datetime);
                const time = PlaygroundBookingCalendar.normalizeTime(datetime);
                return {
                    date: date,
                    time: time,
                    value: (date + 'T' + time),
                    notSelected: undefined,
                    available: undefined
                }
            };
            const farPrev = buildByOffset(-60);
            const prev = buildByOffset(-30);
            const next = buildByOffset(+30);
            const farNext = buildByOffset(+60);
            this.setState(prevState => {
                const reservation = prevState.reservation;
                const schedule = prevState.schedule;
                const timeList = prevState.timeList;
                const checkActive = (prevState.halfHourAvailable && prevState.fullHourRequired);
                if (checkActive) {
                    const updateByData = (element) => {
                        element.selected = (reservation.indexOf(element.value) >= 0);
                        element.available = ((timeList.indexOf(element.time) >= 0) && schedule.get(element.date) && (schedule.get(element.date)).get(element.time));
                    };
                    updateByData(farPrev);
                    updateByData(prev);
                    updateByData(next);
                    updateByData(farNext);
                }
                const idx = reservation.indexOf(value);
                if ((checked) && (idx < 0)) {
                    if (checkActive && !next.selected && !prev.selected) {
                        if (next.available && !next.selected) {
                            reservation.push(value);
                            reservation.push(next.value);
                        } else if (prev.available && !prev.selected) {
                            reservation.push(value);
                            reservation.push(prev.value);
                        } else {
                            console.warn('WARNING: The selected time cell is incorrect!');
                        }
                    } else reservation.push(value);
                } else {
                    reservation.splice(idx, 1);
                    const needNextRemoving = (next.selected && !farNext.selected);
                    const needPrevRemoving = (prev.selected && !farPrev.selected);
                    if (checkActive && (needNextRemoving || needPrevRemoving)) {
                        if (needNextRemoving) reservation.splice(reservation.indexOf(next.value), 1);
                        if (needPrevRemoving) reservation.splice(reservation.indexOf(prev.value), 1);
                    }
                }
                saveReservation(this.playgroundId, this.playgroundVersion, reservation);
                return {reservation: reservation};
            });
        }

        submit(event) {
            event.preventDefault();
            Playground.doReserve(this.playgroundId, this.state.reservation)
                .then(orderId => {
                    console.debug('PlaygroundBookingCalendar', 'submit', 'success');
                    this.modal.activate('hide');
                    clearReservation(
                        this.playgroundId,
                        this.playgroundVersion
                    );
                    setTimeout(() => {
                        this.setState({reservation: []});
                        this.props.history.push(`/order/id${orderId}`);
                    }, env.ANIMATION_TIMEOUT);
                })
                .catch(error => {
                    console.error('PlaygroundBookingCalendar', 'submit', 'failed');
                });
        }

        render() {

            const cancel = event => {
                this.setState({reservation: []});
                saveReservation(this.playgroundId, this.playgroundVersion, []);
            };

            const headerLineBuilder = dateList => {
                const headerLine = [];
                if ((dateList != null) && (dateList.length > 0)) {
                    dateList.forEach((value, index) => {
                        headerLine.push(
                            <th key={index} className="th-calendar">
                                <div className="small">{value.month}</div>
                                <div>
                                    <span className="mr-1">{value.date.split('-')[2]}</span>
                                    <span className="ml-1">{value.dayOfWeek.short.toUpperCase()}</span>
                                </div>
                            </th>
                        );
                    });
                }
                return headerLine;
            };

            const tableBuilder = (timeList, price, schedule) => {
                const table = [];
                const checked = this.state.fullHourRequired && this.state.halfHourAvailable;
                const reservation = this.state.reservation;
                const checkedStyle = this.state.access ? this.state.owner ? 'btn-primary' : 'btn-success' : 'btn-warning';
                const updateReservation = this.updateReservation.bind(this);
                timeList.forEach((time, index, array) => {
                    const rows = [(<td key={0}><span className="badge badge-secondary">{time}</span></td>)];
                    schedule.forEach((value, date) => {
                        const datetime = date + 'T' + time;

                        const content = price ? (
                            <span>{price}<i className="fa fa-rub ml-1"/></span>
                        ) : (
                            <span className="px-3"><i className="fa fa-circle-thin"/></span>
                        );

                        rows.push(
                            <td key={rows.length}>
                                {(value.get(time) && !(checked && !value.get(array[index - 1]) && !value.get(array[index + 1]))) ? (
                                    <CheckButton value={datetime}
                                                 sizeStyle="btn-sm"
                                                 checkedStyle={checkedStyle}
                                                 uncheckedStyle={'btn-outline-dark'}
                                                 id={`pg_booking_calendar_button_${datetime}`}
                                                 checked={!(reservation.indexOf(datetime) < 0)}
                                                 onChange={updateReservation}>
                                        {content}
                                    </CheckButton>
                                ) : (
                                    <button className="btn btn-sm btn-light disabled"
                                            disabled="disabled">
                                        {content}
                                    </button>
                                )}
                            </td>
                        )
                    });
                    table.push(<tr key={index}>{rows}</tr>)
                });
                return (table);
            };

            const owner = this.state.owner;
            const access = this.state.access;
            const contact = this.state.contact;
            const schedule = this.state.schedule;
            const reservation = this.state.reservation;
            const tested = access && !owner && this.state.tested;
            const disabled = tested || !(reservation.length > 0);

            const price = !this.state.freed ? this.state.price : null;
            const totalPrice = ((reservation != null) && (price != null)) ? reservation.length * price : null;

            if (schedule != null) {
                return (
                    <form className="PlaygroundBookingCalendar" onSubmit={this.submit.bind(this)}>
                        <table className="table table-hover">
                            <thead className="thead-dark">
                            <tr>
                                <th className="th-control">
                                    {(this.timeFrame.offset.start > 0) ? (
                                        <button type="button" id="btn-prev" className="btn btn-sm btn-outline-light"
                                                title="Назад" onClick={this.handleOffset.bind(this)}>
                                            <i className="fa fa-angle-left"/>
                                        </button>
                                    ) : (
                                        <button disabled="disabled"
                                                className="btn btn-sm btn-outline-secondary disabled"
                                                title="Назад">
                                            <i className="fa fa-angle-left"/>
                                        </button>
                                    )}
                                    <button type="button" id="btn-next" className="btn btn-sm btn-outline-light"
                                            title="Вперед" onClick={this.handleOffset.bind(this)}>
                                        <i className="fa fa-angle-right"/>
                                    </button>
                                </th>
                                {headerLineBuilder(this.state.dateList)}
                            </tr>
                            </thead>
                            <tbody>
                            {tableBuilder(this.state.timeList, price, schedule)}
                            </tbody>
                        </table>
                        <div className="order-group">
                            {(!access) ? (
                                <AuthAlert link="/login" onClick={this.props.mainframe.showLogin}/>
                            ) : (tested) ? (
                                <TestAlert contact={contact}/>
                            ) : (null)}
                            <div className="btn-group">
                                <CancelButton disabled={!(reservation.length > 0)} onClick={cancel}
                                              title={PlaygroundBookingCalendar.CANCEL_TITLE}/>
                                <SubmitButton access={access}
                                              disabled={disabled}
                                              onForbidden={this.props.mainframe.showLogin}
                                              ownerTitle={PlaygroundBookingCalendar.OWNER_SUBMIT_TITLE}
                                              userTitle={PlaygroundBookingCalendar.USER_SUBMIT_TITLE}
                                              dataTarget={'submitOrder'}
                                              dataToggle={'modal'}
                                              price={totalPrice}
                                              owner={owner}/>
                            </div>
                        </div>
                        <PlaygroundSubmitOrderModal closeTitle={PlaygroundBookingCalendar.CLOSE_TITLE}
                                                    userTitle={PlaygroundBookingCalendar.USER_SUBMIT_TITLE}
                                                    ownerTitle={PlaygroundBookingCalendar.OWNER_SUBMIT_TITLE}
                                                    submitId="submitOrder" reservation={reservation}
                                                    ref={modal => this.modal = modal}
                                                    owner={owner} price={totalPrice}/>
                    </form>
                )
            } else {
                return null;
            }
        }
    })
)

function SubmitButton(props) {
    let finalClass = `btn btn-${props.access ? props.owner ? 'primary' : 'success' : 'warning'}`;
    if (props.disabled) finalClass += ' disabled';
    return (
        <button type="button"
                disabled={props.disabled}
                data-toggle={props.disabled || !props.access ? undefined : props.dataToggle}
                data-target={props.disabled || !props.access ? undefined : `#${props.dataTarget}`}
                onClick={!props.access ? props.onForbidden : undefined}
                className={finalClass}>
            {(props.owner) ? (
                props.ownerTitle
            ) : (
                props.userTitle
            )}
            {(props.owner || !props.price) ? (
                null // no price in this case
            ) : (
                <span className="badge badge-dark ml-1">
                    {props.price}<i className="fa fa-rub ml-1"/>
                </span>
            )}
        </button>
    );
}

function CancelButton(props) {
    return (
        <button type="button" disabled={props.disabled}
                className={props.disabled ? 'btn btn-danger disabled' : 'btn btn-danger'}
                onClick={props.disabled ? undefined : props.onClick}>
            {props.title}
        </button>
    );
}

function TestAlert(props) {
    return (
        <div className="alert alert-warning">
            <b>Внимание!</b> Онлайн бронирование для этой площадки временно недоступно. Чтобы забронировать
            площадку, позвоните <b>по номеру {props.contact}</b> и совершите заказ.
        </div>
    )
}

function AuthAlert(props) {
    return (
        <div className="alert alert-danger">
            Необходимо <Link to={props.link} className="alert-link" onClick={props.onClick}>авторизироваться</Link> для
            того, чтобы сделать заказ!
        </div>
    )
}