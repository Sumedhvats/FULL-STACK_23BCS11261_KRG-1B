import React from 'react'
import './FruitList.css'

const FruitList = ({ fruits = [] }) => {

	return (
		<ul className="fruit-list">
			{fruits.map((fruit, i) => (
				<li
					key={i}
					className="fruit-item"
					onClick={() => alert(fruit)}
				>
					{fruit}
				</li>
			))}
		</ul>
	)
}

export default FruitList

