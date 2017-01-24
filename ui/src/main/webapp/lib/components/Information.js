import { informationDiv, informationLabel, informationP, informationListItemP } from './styles.less'

export default ({ id, label, value }) => (
  <div className={informationDiv}>
    <label className={informationLabel}>{label}</label>
    { value instanceof Array
      ? (
        <div className={informationP}>
          {value.map((text, i) => (
            <p key={i} className={informationListItemP}>
              {text}
            </p>))}
        </div>
        )
      : (<p id={id} className={informationP}>{value}</p>)
    }
  </div>
)
