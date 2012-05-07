<stockTicker xmlns="http://mycorp.stocks.com">
	<time>${body[0][5]}:T${body[0][6]}</time>
	<stockName>${body[0][0]}</stockName>
	<price currency="${body[0][1]}">
		<current>${body[0][4]}</current>
	</price>
</stockTicker>