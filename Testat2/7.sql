SELECT customer, count(customer) FROM review
GROUP BY customer
HAVING COUNT(customer) >= 10