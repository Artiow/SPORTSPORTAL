import React from 'react';
import {Link} from 'react-router-dom';
import './NoMatch.css';

export default function NoMatch() {
    return (
        <div className="NoMatch page-wrap d-flex flex-row align-items-center">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-12 text-center">
                        <span className="d-block display-1">404</span>
                        <div className="mb-4 lead">Страница, которую вы ищите, не найдена.</div>
                        <Link to={'/'} className="btn btn-link">На домашнюю страницу</Link>
                    </div>
                </div>
            </div>
        </div>
    );
}