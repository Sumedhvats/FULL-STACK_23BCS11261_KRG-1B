import React from 'react';
import Student from '../classes/Student';
import Teacher from '../classes/Teacher';

const PersonCard = ({ person, onDelete }) => {
  let role = 'Person';
  if (person instanceof Student) role = 'Student';
  else if (person instanceof Teacher) role = 'Teacher';

  return (
    <div className={`card ${role.toLowerCase()}`}>
      <h2>{person.name}</h2>
      <p>{person.describe()}</p>
      <span className="role">{role}</span>
      <button className="delete-btn" onClick={onDelete}>
        🗑 Delete
      </button>
    </div>
  );
};

export default PersonCard;
